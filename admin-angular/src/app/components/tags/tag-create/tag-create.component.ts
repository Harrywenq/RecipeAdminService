import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment'; // Đảm bảo đúng đường dẫn environment
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { NgForm } from '@angular/forms';

// DTO cho Input Tag (chỉ có 'name')
interface TagInput {
  name: string;
}

@Component({
  selector: 'app-tag-create',
  templateUrl: './tag-create.component.html',
  styleUrls: ['./tag-create.component.scss']
})
export class TagCreateComponent implements OnInit {
  newTag: TagInput = {
    name: ''
  };

  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  private apiUrl = `${environment.apiBaseUrl}/api/tags`;
  private token: string = ''; // Giả định có thể cần token

  constructor(
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Lấy token từ localStorage (nếu API yêu cầu xác thực)
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage = 'Bạn chưa đăng nhập hoặc phiên đã hết hạn. Vui lòng đăng nhập để tạo tag.';
      // this.router.navigate(['/login']); // Có thể điều hướng về trang đăng nhập
    }
  }

  onSubmit(form: NgForm): void {
    if (form.invalid) {
      this.errorMessage = 'Vui lòng điền đầy đủ các thông tin bắt buộc.';
      return;
    }

    this.loading = true;
    this.errorMessage = null; // Xóa lỗi cũ
    this.successMessage = null; // Xóa thông báo thành công cũ

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.token}` // Thêm Authorization header nếu có token
    });

    // Sử dụng POST request để tạo tag mới
    // responseType: 'text' vì backend trả về ResponseEntity.ok("Successful") là một chuỗi
    this.http.post(this.apiUrl, this.newTag, { headers, responseType: 'text' })
      .pipe(
        finalize(() => this.loading = false), // Luôn đặt loading = false sau khi request hoàn thành
        catchError(err => {
          console.error('Lỗi khi tạo tag:', err);
          let serverErrorMessage = 'Không thể kết nối đến server. Vui lòng thử lại sau.';

          if (err.error && typeof err.error === 'string') {
            serverErrorMessage = err.error; // Backend trả về chuỗi lỗi như 'TAG_NAME_EXISTED'
          } else if (err.error && err.error.message) {
            serverErrorMessage = err.error.message; // Backend trả về JSON với trường message
          } else if (err.statusText) {
            serverErrorMessage = `Lỗi: ${err.status} - ${err.statusText}`;
          }

          if (err.status === 409 || serverErrorMessage.includes("TAG_NAME_EXISTED")) {
            this.errorMessage = `Tên tag "${this.newTag.name}" đã tồn tại. Vui lòng chọn tên khác.`;
          } else if (err.status === 401) {
            this.errorMessage = 'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = serverErrorMessage;
          }
          return throwError(err); // Quan trọng để re-throw lỗi
        })
      )
      .subscribe(
        (response: string) => { // response sẽ là chuỗi "Successful"
          this.successMessage = 'Tạo tag thành công!';
          form.resetForm(); // Reset form sau khi tạo thành công
          this.newTag = { name: '' }; // Đặt lại model
          setTimeout(() => {
            this.router.navigate(['/tags']); // Điều hướng về trang danh sách tag sau 2 giây
          }, 2000);
        }
      );
  }

  goBackToList(): void {
    this.router.navigate(['/tags']); // Quay lại trang danh sách tag
  }
}