import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.scss']
})
export class UserDetailComponent implements OnInit {
  userDetails: any = null;
  token: string = '';
  errorMessage: string = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    // Lấy token với key 'access_token' thay vì 'token'
    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage = 'Không tìm thấy token. Vui lòng đăng nhập lại.';
      return;
    }

    this.fetchUserDetails();
  }

  fetchUserDetails() {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${this.token}`
    });

    this.http.post<any>(`${environment.apiBaseUrl}/details`, {}, { headers })
      .subscribe({
        next: (response) => {
          this.userDetails = response;
        },
        error: (error) => {
          console.error('Error fetching user details:', error);
          this.errorMessage = 'Lỗi khi lấy thông tin user: ' + (error.statusText || 'Không thể kết nối đến server');
        }
      });
  }

   navigateToUpdate() {
    if (this.userDetails && this.userDetails.id) {
      this.router.navigate(['/user-update', this.userDetails.id]);
    } else {
      this.errorMessage = 'Không thể cập nhật tài khoản: Không có ID người dùng.';
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}