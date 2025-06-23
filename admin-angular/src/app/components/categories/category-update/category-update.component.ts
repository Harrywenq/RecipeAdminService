import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router'; // Import ActivatedRoute và Router
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { NgForm } from '@angular/forms';
import { environment } from 'src/app/environments/environment';

// DTO cho input (tạo và cập nhật)
interface RecipeCategoryInput {
  name: string;
  description: string;
}

// DTO cho output (khi lấy dữ liệu để hiển thị)
interface RecipeCategoryOutput {
  id: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-category-update',
  templateUrl: './category-update.component.html',
  styleUrls: ['./category-update.component.scss']
})
export class CategoryUpdateComponent implements OnInit {
  categoryId: number | null = null;
  // Khởi tạo updateCategory với giá trị mặc định để tránh lỗi undefined
  updateCategory: RecipeCategoryInput = {
    name: '',
    description: ''
  };

  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  private apiUrl = `${environment.apiBaseUrl}/api/recipe-categories`;
  private token: string = '';

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute, // Để lấy ID từ URL
    private router: Router // Để điều hướng
  ) { }

  ngOnInit(): void {
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage = 'Bạn chưa đăng nhập hoặc phiên đã hết hạn. Vui lòng đăng nhập để thực hiện thao tác.';
      // this.router.navigate(['/login']); // Điều hướng nếu cần
      return;
    }

    // Lấy ID từ URL
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.categoryId = +idParam; // Chuyển đổi string sang number
        this.loadCategoryDetails(this.categoryId);
      } else {
        this.errorMessage = 'Không tìm thấy ID danh mục. Vui lòng quay lại trang danh sách.';
      }
    });
  }

  loadCategoryDetails(id: number): void {
    this.loading = true;
    this.errorMessage = null;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });

    this.http.get<RecipeCategoryOutput>(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        finalize(() => this.loading = false),
        catchError(err => {
          console.error('Lỗi khi tải chi tiết danh mục:', err);
          this.errorMessage = 'Không thể tải chi tiết danh mục: ' + (err.error?.message || err.statusText || 'Không thể kết nối đến server.');
          if (err.status === 404) {
            this.errorMessage = 'Danh mục không tồn tại hoặc đã bị xóa.';
          } else if (err.status === 401) {
            this.errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
            // this.router.navigate(['/login']);
          }
          return throwError(err);
        })
      )
      .subscribe(
        (data: RecipeCategoryOutput) => {
          this.updateCategory.name = data.name;
          this.updateCategory.description = data.description;
        }
      );
  }

  onSubmit(form: NgForm): void {
    if (!this.categoryId || form.invalid) {
      this.errorMessage = 'Thông tin danh mục không hợp lệ hoặc thiếu. Vui lòng kiểm tra lại.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.token}`
    });

    // **Sử dụng PUT request để cập nhật**
    this.http.put(`${this.apiUrl}/${this.categoryId}`, this.updateCategory, { headers, responseType: 'text' })
      .pipe(
        finalize(() => this.loading = false),
        catchError(err => {
          console.error('Lỗi khi cập nhật danh mục:', err);
          let serverErrorMessage = 'Không thể kết nối đến server. Vui lòng thử lại sau.';

          if (err.error && typeof err.error === 'string') {
            serverErrorMessage = err.error; // Backend trả về chuỗi lỗi
          } else if (err.error && err.error.message) {
            serverErrorMessage = err.error.message; // Backend trả về JSON với message
          } else if (err.statusText) {
            serverErrorMessage = `Lỗi: ${err.status} - ${err.statusText}`;
          }

          if (err.status === 409 || serverErrorMessage.includes("RECIPE_CATEGORY_NAME_EXISTED")) {
            this.errorMessage = `Tên danh mục "${this.updateCategory.name}" đã tồn tại. Vui lòng chọn tên khác.`;
          } else if (err.status === 404) {
            this.errorMessage = 'Danh mục không tồn tại hoặc đã bị xóa.';
          } else if (err.status === 401) {
            this.errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = serverErrorMessage;
          }
          return throwError(err);
        })
      )
      .subscribe(
        (response: string) => { // response sẽ là chuỗi "Successful"
          this.successMessage = 'Cập nhật danh mục thành công!';
          setTimeout(() => {
            this.router.navigate(['/categories']); // Quay lại trang danh sách sau 2 giây
          }, 2000);
        }
      );
  }

  onDelete(): void {
    if (!this.categoryId) {
      this.errorMessage = 'Không có ID danh mục để xóa.';
      return;
    }

    if (!confirm('Bạn có chắc chắn muốn xóa danh mục này? Hành động này không thể hoàn tác!')) {
      return; // Người dùng hủy xóa
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });

    // **Sử dụng DELETE request để xóa**
    this.http.delete(`${this.apiUrl}/${this.categoryId}`, { headers, responseType: 'text' })
      .pipe(
        finalize(() => this.loading = false),
        catchError(err => {
          console.error('Lỗi khi xóa danh mục:', err);
          let serverErrorMessage = 'Không thể kết nối đến server. Vui lòng thử lại sau.';

          if (err.error && typeof err.error === 'string') {
            serverErrorMessage = err.error;
          } else if (err.error && err.error.message) {
            serverErrorMessage = err.error.message;
          } else if (err.statusText) {
            serverErrorMessage = `Lỗi: ${err.status} - ${err.statusText}`;
          }

          if (err.status === 404) {
            this.errorMessage = 'Danh mục không tồn tại hoặc đã bị xóa.';
          } else if (err.status === 401) {
            this.errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = serverErrorMessage;
          }
          return throwError(err);
        })
      )
      .subscribe(
        (response: string) => { // response sẽ là chuỗi "Deleted successfully!"
          this.successMessage = 'Xóa danh mục thành công!';
          setTimeout(() => {
            this.router.navigate(['/categories']); // Quay lại trang danh sách sau 2 giây
          }, 2000);
        }
      );
  }

  goBackToList(): void {
    this.router.navigate(['/categories']);
  }
}