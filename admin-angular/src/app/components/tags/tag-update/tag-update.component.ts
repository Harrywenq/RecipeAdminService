import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http'; // Import HttpErrorResponse
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { catchError, finalize } from 'rxjs/operators'; // Import catchError, finalize
import { throwError } from 'rxjs'; // Import throwError

@Component({
  selector: 'app-tag-update',
  templateUrl: './tag-update.component.html',
  styleUrls: ['./tag-update.component.scss'],
})
export class TagUpdateComponent implements OnInit {
  tagId: number | null = null;
  tagName: string = '';
  message: string = ''; // Được dùng cho cả success và error
  isSuccess: boolean = false;
  loading: boolean = true;

  private apiUrl = `${environment.apiBaseUrl}/api/tags`;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (id) {
        this.tagId = +id;
        this.fetchTagDetails();
      } else {
        this.loading = false;
        this.showMessage(
          'Không tìm thấy ID tag để cập nhật. Vui lòng chọn tag từ danh sách.',
          false
        );
      }
    });
  }

  fetchTagDetails(): void {
    if (this.tagId === null) {
      this.loading = false;
      return;
    }

    this.loading = true;
    this.http
      .get<any>(`${this.apiUrl}/${this.tagId}`)
      .pipe(
        finalize(() => (this.loading = false)), // Đảm bảo loading = false
        catchError((error: HttpErrorResponse) => {
          console.error('Lỗi khi tải thông tin tag:', error);
          let errorMessage = 'Không thể tải thông tin tag. Vui lòng thử lại.';
          if (error.error && typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (
            error.error &&
            typeof error.error === 'object' &&
            error.error.message
          ) {
            errorMessage = error.error.message;
          } else if (error.error) {
            errorMessage = JSON.stringify(error.error); // Chuyển object lỗi thành chuỗi
          } else if (error.statusText) {
            errorMessage = `Lỗi: ${error.status} - ${error.statusText}`;
          }

          this.showMessage(errorMessage, false);
          this.router.navigate(['/tags']); // Quay về danh sách nếu lỗi
          return throwError(() => error);
        })
      )
      .subscribe((tag: any) => {
        this.tagName = tag.name;
      });
  }

  onUpdate(): void {
    if (this.tagId === null) {
      this.showMessage('Không có ID tag để cập nhật.', false);
      return;
    }

    if (!this.tagName.trim()) {
      this.showMessage('Vui lòng nhập tên tag mới.', false);
      return;
    }

    const tagInput = { name: this.tagName };
    // Giả định backend trả về chuỗi "Successful" hoặc một đối tượng có thuộc tính "message"
    this.http
      .put(`${this.apiUrl}/${this.tagId}`, tagInput, { responseType: 'text' }) // Vẫn giữ responseType: 'text' nếu backend trả về chuỗi
      .pipe(
        catchError((error: HttpErrorResponse) => {
          // Type HttpErrorResponse
          console.error('Lỗi khi cập nhật tag:', error);
          let errorMessage =
            'Đã có lỗi xảy ra khi cập nhật tag. Vui lòng thử lại.';

          // Xử lý lỗi trả về từ server
          if (typeof error.error === 'string') {
            errorMessage = error.error; // Backend trả về chuỗi lỗi như 'TAG_NAME_EXISTED'
          } else if (
            error.error &&
            typeof error.error === 'object' &&
            error.error.message
          ) {
            errorMessage = error.error.message; // Backend trả về JSON với trường message
          } else if (error.error) {
            errorMessage = JSON.stringify(error.error); // Chuyển object lỗi thành chuỗi
          } else if (error.statusText) {
            errorMessage = `Lỗi: ${error.status} - ${error.statusText}`;
          }

          if (
            error.status === 409 ||
            errorMessage.includes('TAG_NAME_EXISTED')
          ) {
            this.showMessage(
              `Tên tag "${this.tagName}" đã tồn tại. Vui lòng chọn tên khác.`,
              false
            );
          } else {
            this.showMessage(errorMessage, false);
          }
          return throwError(() => error);
        })
      )
      .subscribe(
        (response: string) => {
          // response sẽ là chuỗi "Successful"
          // CHỈNH SỬA Ở ĐÂY ĐỂ ĐẢM BẢO HIỂN THỊ THÔNG BÁO THÀNH CÔNG RÕ RÀNG
          let successMsg = 'Cập nhật tag thành công!';
          if (
            response &&
            response.length > 0 &&
            response.toLowerCase().includes('successful')
          ) {
            // Nếu backend trả về "Successful" hoặc một chuỗi cụ thể
            successMsg = `Tag "${this.tagName}" đã được cập nhật thành công!`;
          }
          // Nếu response là một JSON object (mà bạn không đặt responseType: 'text')
          // và có thuộc tính message/name, thì bạn sẽ xử lý như sau:
          // else if (typeof response === 'object' && response !== null && response.message) {
          //     successMsg = response.message;
          // } else if (typeof response === 'object' && response !== null && response.name) {
          //      successMsg = `Tag "${response.name}" đã được cập nhật thành công!`;
          // }

          this.showMessage(successMsg, true);
          // Không reset tagId hay tagName ở đây để người dùng có thể tiếp tục chỉnh sửa hoặc thấy tên đã cập nhật
          // Sau khi cập nhật thành công, có thể tự động quay lại trang danh sách hoặc làm mới dữ liệu
          // setTimeout(() => {
          //     this.router.navigate(['/tags']);
          // }, 2000);
        }
        // Bạn không cần block error handler thứ hai ở đây nếu đã dùng catchError trong pipe
      );
  }

  goBack(): void {
    this.router.navigate(['/tags']);
  }

  private showMessage(msg: string, success: boolean): void {
    this.message = msg;
    this.isSuccess = success;
    setTimeout(() => {
      this.message = '';
    }, 3000);
  }
}
