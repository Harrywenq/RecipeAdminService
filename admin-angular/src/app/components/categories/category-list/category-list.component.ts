import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment'; // Đã sửa đường dẫn environment

interface RecipeCategoryOutput {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss'],
})
export class CategoryListComponent implements OnInit {
  categories: RecipeCategoryOutput[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  searchCategoryName: string = '';

  private apiUrl = `${environment.apiBaseUrl}/api/recipe-categories`;
  private token: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    // Đảm bảo lấy token nếu API yêu cầu xác thực
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage = 'Không tìm thấy token. Vui lòng đăng nhập lại.';
      // Có thể chuyển hướng về trang đăng nhập nếu token không tồn tại
      // this.router.navigate(['/login']);
      // return;
    }

    this.getCategories();
  }

  getCategories(): void {
    this.loading = true;
    this.errorMessage = '';

    let headers = new HttpHeaders();
    if (this.token) {
      // Chỉ thêm Authorization header nếu có token
      headers = headers.set('Authorization', `Bearer ${this.token}`);
    }

    let params = new HttpParams();
    if (this.searchCategoryName) {
      params = params.set('name', this.searchCategoryName);
    }

    this.http
      .get<RecipeCategoryOutput[]>(this.apiUrl, {
        headers: headers,
        params: params,
      })
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((err) => {
          console.error('Lỗi khi lấy danh sách danh mục:', err);
          this.errorMessage =
            'Lỗi khi lấy danh sách danh mục: ' +
            (err.error?.message ||
              err.statusText ||
              'Không thể kết nối đến server.');

          if (err.status === 401) {
            this.errorMessage =
              'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            // this.router.navigate(['/login']);
          } else if (err.status === 400) {
            this.errorMessage =
              'Yêu cầu không hợp lệ. Vui lòng kiểm tra lại dữ liệu tìm kiếm.';
          }
          return throwError(err);
        })
      )
      .subscribe((data: RecipeCategoryOutput[]) => {
        this.categories = data;
      });
  }

  onSearch(): void {
    this.getCategories();
  }

  goToCreateCategory(): void {
    this.router.navigate(['/categories/create']);
  }

  // Hàm điều hướng đến trang chỉnh sửa
  navigateToEdit(id: number): void {
    this.router.navigate(['/categories/edit', id]); // Route phải khớp với cấu hình trong app-routing.module.ts
  }

  // Hàm xác nhận và gọi API xóa
  confirmDelete(id: number, name: string): void {
    if (
      confirm(
        `Bạn có chắc chắn muốn xóa danh mục "${name}" này không? Hành động này không thể hoàn tác!`
      )
    ) {
      this.deleteCategory(id);
    }
  }

  // Hàm thực hiện xóa danh mục
  private deleteCategory(id: number): void {
    this.loading = true; // Hiển thị loading khi xóa
    this.errorMessage = '';
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .delete(`${this.apiUrl}/${id}`, { headers, responseType: 'text' }) // Sử dụng responseType: 'text'
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((err) => {
          console.error('Lỗi khi xóa danh mục:', err);
          let serverErrorMessage =
            'Không thể xóa danh mục: ' +
            (err.error?.message ||
              err.statusText ||
              'Không thể kết nối đến server.');

          if (err.status === 404) {
            serverErrorMessage = 'Danh mục không tồn tại hoặc đã bị xóa.';
          } else if (err.status === 401) {
            serverErrorMessage =
              'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
            // this.router.navigate(['/login']);
          }
          this.errorMessage = serverErrorMessage;
          return throwError(err);
        })
      )
      .subscribe(() => {
        // Xóa thành công, hiển thị thông báo và tải lại danh sách
        alert('Danh mục đã được xóa thành công!');
        this.getCategories(); // Tải lại danh sách để cập nhật giao diện
      });
  }
}
