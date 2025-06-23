import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { NgForm } from '@angular/forms';
import { environment } from 'src/app/environments/environment';

interface RecipeCategoryInput {
  name: string;
  description: string;
}

@Component({
  selector: 'app-category-create',
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.scss'],
})
export class CategoryCreateComponent implements OnInit {
  newCategory: RecipeCategoryInput = {
    name: '',
    description: '',
  };

  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  private apiUrl = `${environment.apiBaseUrl}/api/recipe-categories`;
  private token: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage =
        'Bạn chưa đăng nhập hoặc phiên đã hết hạn. Vui lòng đăng nhập để tạo danh mục.';
      // this.router.navigate(['/login']);
    }
  }

  onSubmit(form: NgForm): void {
    if (form.invalid) {
      this.errorMessage = 'Vui lòng điền đầy đủ các thông tin bắt buộc.';
      return;
    }

    this.loading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    // **Thay đổi ở đây: Thêm { responseType: 'text' as 'json' }**
    // responseType: 'text' sẽ yêu cầu Angular không parse JSON mà xử lý như văn bản
    // 'as 'json'' là một cách để vượt qua lỗi type checking của TypeScript,
    // vì `post<string>` mong đợi JSON, nhưng ta lại chỉ định text.
    // Thực tế, bạn nên đổi kiểu dữ liệu generic cho POST thành `any` hoặc `Object`
    // hoặc remove <string> và để TypeScript tự suy luận hoặc chỉ rõ kiểu trả về `Observable<string>`
    // nếu bạn muốn strict.
    // Nhưng cách đơn giản nhất để fix lỗi ngay lập tức là:
    this.http
      .post(this.apiUrl, this.newCategory, { headers, responseType: 'text' }) // Bỏ <string> hoặc đặt <any>
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((err) => {
          console.error('Lỗi khi tạo danh mục:', err);
          let serverErrorMessage =
            'Không thể kết nối đến server. Vui lòng thử lại sau.';

          if (err.error instanceof ErrorEvent) {
            // Client-side error
            serverErrorMessage = `Lỗi mạng: ${err.error.message}`;
          } else if (err.error && typeof err.error === 'string') {
            // Nếu lỗi trả về cũng là text (ví dụ: 'RECIPE_CATEGORY_NAME_EXISTED')
            serverErrorMessage = err.error;
          } else if (err.error && err.error.message) {
            // Nếu lỗi trả về là JSON với trường message (phổ biến hơn)
            serverErrorMessage = err.error.message;
          } else if (err.statusText) {
            serverErrorMessage = `Lỗi: ${err.status} - ${err.statusText}`;
          }

          if (
            err.status === 409 ||
            serverErrorMessage.includes('RECIPE_CATEGORY_NAME_EXISTED')
          ) {
            this.errorMessage = `Tên danh mục "${this.newCategory.name}" đã tồn tại. Vui lòng chọn tên khác.`;
          } else if (err.status === 401) {
            this.errorMessage =
              'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else {
            this.errorMessage = serverErrorMessage;
          }
          return throwError(err);
        })
      )
      .subscribe((response: string) => {
        // response sẽ là chuỗi "Successful"
        this.successMessage = 'Tạo danh mục thành công!';
        form.resetForm();
        this.newCategory = { name: '', description: '' };
        setTimeout(() => {
          this.router.navigate(['/categories']);
        }, 2000);
      });
  }

  goBackToList(): void {
    this.router.navigate(['/categories']);
  }
}
