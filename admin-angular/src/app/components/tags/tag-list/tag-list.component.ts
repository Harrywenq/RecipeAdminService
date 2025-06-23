import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';

interface TagOutput {
  id: number;
  name: string;
}

@Component({
  selector: 'app-tag-list',
  templateUrl: './tag-list.component.html',
  styleUrls: ['./tag-list.component.scss'],
})
export class TagListComponent implements OnInit {
  tags: TagOutput[] = [];
  loading: boolean = false;
  errorMessage: string | null = null;
  searchTagName: string = '';

  private apiUrl = `${environment.apiBaseUrl}/api/tags`;
  private token: string = ''; // Giả định có thể cần token cho các API sau này

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // Lấy token từ localStorage (nếu API yêu cầu xác thực)
    // Lưu ý: Đảm bảo bạn đã lưu token vào localStorage với key 'access_token' khi đăng nhập
    this.token = localStorage.getItem('access_token') || '';
    // if (!this.token) {
    //   this.errorMessage = 'Bạn chưa đăng nhập hoặc phiên đã hết hạn. Vui lòng đăng nhập để xem danh sách.';
    //   // this.router.navigate(['/login']); // Có thể điều hướng về trang đăng nhập
    //   // return; // Dừng nếu không có token và không thể tiếp tục
    // }

    this.getTags(); // Tải danh sách tag khi component khởi tạo
  }

  getTags(): void {
    this.loading = true;
    this.errorMessage = null; // Reset thông báo lỗi

    let headers = new HttpHeaders();
    if (this.token) {
      headers = headers.set('Authorization', `Bearer ${this.token}`);
    }

    let params = new HttpParams();
    // Thêm tham số 'name' vào request nếu có giá trị tìm kiếm
    if (this.searchTagName.trim()) {
      // Sử dụng .trim() để loại bỏ khoảng trắng thừa
      params = params.set('name', this.searchTagName.trim());
    }

    this.http
      .get<TagOutput[]>(this.apiUrl, { headers: headers, params: params })
      .pipe(
        finalize(() => (this.loading = false)), // Luôn đặt loading = false sau khi request hoàn thành
        catchError((err) => {
          console.error('Lỗi khi lấy danh sách tag:', err);
          let serverErrorMessage =
            'Không thể kết nối đến server. Vui lòng thử lại sau.';

          if (err.error && err.error.message) {
            serverErrorMessage = err.error.message;
          } else if (err.statusText) {
            serverErrorMessage = `Lỗi: ${err.status} - ${err.statusText}`;
          }

          if (err.status === 401) {
            serverErrorMessage =
              'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            // this.router.navigate(['/login']); // Điều hướng về trang đăng nhập nếu token hết hạn/không hợp lệ
          }
          this.errorMessage = serverErrorMessage;
          return throwError(err);
        })
      )
      .subscribe((data: TagOutput[]) => {
        this.tags = data;
        if (this.tags.length === 0 && this.searchTagName.trim()) {
          this.errorMessage = `Không tìm thấy tag nào với tên "${this.searchTagName}".`;
        } else if (this.tags.length === 0) {
          this.errorMessage = 'Chưa có tag nào được tạo.';
        }
      });
  }

  onSearch(): void {
    // Gọi lại hàm getTags để tải dữ liệu với tham số tìm kiếm mới
    this.getTags();
  }

  goToCreateTag(): void {
    this.router.navigate(['/tag/create']); // Điều hướng đến trang tạo tag
  }

  // --- Thêm các phương thức cho Sửa và Xóa ---

  /**
   * Điều hướng đến trang cập nhật tag với ID của tag.
   * @param id ID của tag cần chỉnh sửa.
   */
  navigateToEdit(id: number): void {
    this.router.navigate(['/tag/update', id]); // Route phải khớp với cấu hình trong app-routing.module.ts
  }

  /**
   * Xử lý xóa tag.
   * @param id ID của tag cần xóa.
   */
  deleteTag(id: number): void {
    if (
      confirm(
        'Bạn có chắc chắn muốn xóa tag này không? Thao tác này không thể hoàn tác!'
      )
    ) {
      this.loading = true;
      this.errorMessage = null;

      let headers = new HttpHeaders();
      if (this.token) {
        headers = headers.set('Authorization', `Bearer ${this.token}`);
      }

      this.http
        .delete(`${this.apiUrl}/${id}`, {
          headers: headers,
          responseType: 'text',
        }) // responseType 'text' vì backend trả về "Successful"
        .pipe(
          finalize(() => (this.loading = false)),
          catchError((err) => {
            console.error('Lỗi khi xóa tag:', err);
            let serverErrorMessage = 'Đã có lỗi xảy ra khi xóa tag.';
            if (err.error) {
              serverErrorMessage = err.error; // Backend trả về string lỗi trực tiếp
            }
            this.errorMessage = serverErrorMessage;
            return throwError(err);
          })
        )
        .subscribe((response) => {
          console.log('Xóa tag thành công:', response);
          alert('Xóa tag thành công!'); // Thông báo cho người dùng
          this.getTags(); // Tải lại danh sách tag sau khi xóa thành công
        });
    }
  }
}
