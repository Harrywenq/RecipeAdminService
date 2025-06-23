// user-update.component.ts (phiên bản cập nhật để tự lấy userDetails)
import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router'; // Import Router để điều hướng sau khi cập nhật/hủy
import { environment } from 'src/app/environments/environment';

@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.scss'],
})
export class UserUpdateComponent implements OnInit {
  userDetails: any; // userDetails sẽ được tải trong component này
  token: string = '';
  updateErrorMessage: string = '';
  updateSuccessMessage: string = '';

  updateForm: any = {
    displayName: '',
    dateOfBirth: '',
    password: '',
    retypePassword: '',
  };

  constructor(
    private http: HttpClient,
    private router: Router // Inject Router
  ) {}

  ngOnInit() {
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.updateErrorMessage = 'Không tìm thấy token. Vui lòng đăng nhập lại.';
      this.goToLogin(); // Chuyển hướng đến trang đăng nhập nếu không có token
      return;
    }
    this.fetchUserDetailsToUpdate(); // Tải thông tin người dùng khi component khởi tạo
  }

  fetchUserDetailsToUpdate() {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .post<any>(`${environment.apiBaseUrl}/details`, {}, { headers })
      .subscribe({
        next: (response) => {
          this.userDetails = response;
          this.populateForm(); // Điền dữ liệu vào form sau khi tải
          this.updateErrorMessage = '';
        },
        error: (error) => {
          console.error('Error fetching user details for update:', error);
          this.updateErrorMessage =
            'Lỗi khi lấy thông tin user để cập nhật: ' +
            (error.error?.message ||
              error.statusText ||
              'Không thể kết nối đến server');
          if (error.status === 401) {
            this.goToLogin();
          }
        },
      });
  }

  private populateForm() {
    if (this.userDetails) {
      this.updateForm.displayName = this.userDetails.displayName;
      // Chuyển đổi định dạng ngày nếu cần thiết cho input type="date"
      if (
        this.userDetails.dateOfBirth &&
        this.userDetails.dateOfBirth.includes('T')
      ) {
        this.updateForm.dateOfBirth =
          this.userDetails.dateOfBirth.split('T')[0];
      } else {
        this.updateForm.dateOfBirth = this.userDetails.dateOfBirth;
      }
      this.updateForm.password = '';
      this.updateForm.retypePassword = '';
    }
  }

  updateUserProfile() {
    this.updateErrorMessage = '';
    this.updateSuccessMessage = '';

    if (!this.userDetails || !this.userDetails.id) {
      this.updateErrorMessage = 'Không thể tìm thấy ID người dùng để cập nhật.';
      return;
    }

    if (
      this.updateForm.password &&
      this.updateForm.password !== this.updateForm.retypePassword
    ) {
      this.updateErrorMessage = 'Mật khẩu mới và nhập lại mật khẩu không khớp.';
      return;
    }

    const payload: any = {
      displayName: this.updateForm.displayName,
      dateOfBirth: this.updateForm.dateOfBirth,
    };

    if (this.updateForm.password) {
      payload.password = this.updateForm.password;
      payload.retypePassword = this.updateForm.retypePassword;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .put<any>(
        `${environment.apiBaseUrl}/update/${this.userDetails.id}`,
        payload,
        { headers }
      )
      .subscribe({
        next: (response) => {
          if (response && response.message === 'Successful') {
            this.updateSuccessMessage =
              'Cập nhật thông tin tài khoản thành công!';
            // Sau khi cập nhật thành công, có thể chuyển hướng về trang chi tiết
            setTimeout(() => {
              // Dùng setTimeout để người dùng kịp đọc thông báo
              this.router.navigate(['/user-detail']);
            }, 1500);
          } else {
            this.updateErrorMessage =
              response.message ||
              'Cập nhật không thành công. Vui lòng thử lại.';
          }
        },
        error: (error) => {
          console.error('Error updating user profile:', error);
          this.updateErrorMessage =
            error.error?.message ||
            'Lỗi khi cập nhật thông tin: ' +
              (error.statusText || 'Không thể kết nối đến server');
          if (error.status === 401) {
            this.goToLogin();
          }
        },
      });
  }

  cancelUpdate() {
    // Chuyển hướng về trang chi tiết người dùng
    this.router.navigate(['/user-detail']);
  }

  goToLogin() {
    localStorage.removeItem('access_token');
    this.router.navigate(['/login']);
  }
}
