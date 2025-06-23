import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';

interface UserOutput {
  id: number;
  username: string;
  displayName: string;
  dateOfBirth: string; // Hoặc Date nếu bạn muốn chuyển đổi
  roleId: number;
  roleName: string;
}

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
})
export class UserListComponent implements OnInit {
  users: UserOutput[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  searchDisplayName: string = '';

  private apiUrl = `${environment.apiBaseUrl}/get-list`;
  private deleteUserApiUrl = `${environment.apiBaseUrl}`;
  private token: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.token = localStorage.getItem('access_token') || '';

    if (!this.token) {
      this.errorMessage = 'Không tìm thấy token. Vui lòng đăng nhập lại.';
      // this.router.navigate(['/login']);
      return;
    }

    this.getUsers();
  }

  getUsers(): void {
    this.loading = true;
    this.errorMessage = '';

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    let params = new HttpParams();
    if (this.searchDisplayName) {
      params = params.set('displayName', this.searchDisplayName);
    }

    this.http
      .post<UserOutput[]>(this.apiUrl, {}, { headers: headers, params: params })
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((err) => {
          console.error('Lỗi khi lấy danh sách người dùng:', err);
          this.errorMessage =
            'Lỗi khi lấy danh sách người dùng: ' +
            (err.error?.message ||
              err.statusText ||
              'Không thể kết nối đến server.');

          if (err.status === 401) {
            this.errorMessage =
              'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else if (err.status === 400) {
            this.errorMessage =
              'Yêu cầu không hợp lệ. Vui lòng kiểm tra lại dữ liệu.';
          }
          return throwError(err);
        })
      )
      .subscribe((data: UserOutput[]) => {
        this.users = data;
      });
  }

  onSearch(): void {
    this.getUsers();
  }

  // Hàm mới để hiển thị thông báo xác nhận xóa
  confirmDelete(userId: number, userName: string): void {
    const confirmation = confirm(
      `Bạn có chắc chắn muốn xóa tài khoản "${userName}" (ID: ${userId}) không? Sau khi xóa không thể hoàn tác.`
    );

    if (confirmation) {
      console.log(`Người dùng đã xác nhận xóa tài khoản với ID: ${userId}`);
      // Ở đây, bạn sẽ gọi API xóa người dùng thực sự
      // Tạm thời, tôi sẽ để một hàm giả `deleteUser`
      this.deleteUser(userId);
    } else {
      console.log('Người dùng đã hủy thao tác xóa.');
    }
  }

  deleteUser(userId: number): void {
    this.loading = true;
    this.errorMessage = '';

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .delete(`${this.deleteUserApiUrl}/${userId}`, {
        headers: headers,
        responseType: 'text', // ⚠️ THÊM DÒNG NÀY: Nếu server trả về plain text
      })
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((err) => {
          console.error('Lỗi khi xóa người dùng:', err);
          this.errorMessage =
            'Lỗi khi xóa người dùng: ' +
            (err.error?.message ||
              err.statusText ||
              'Không thể kết nối đến server.');

          if (err.status === 401) {
            this.errorMessage =
              'Phiên đăng nhập đã hết hạn hoặc không hợp lệ. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else if (err.status === 400) {
            this.errorMessage =
              'Yêu cầu xóa không hợp lệ. Vui lòng kiểm tra lại dữ liệu.';
          } else if (err.status === 404) {
            this.errorMessage = 'Người dùng không tồn tại hoặc đã bị xóa.';
          }

          return throwError(err);
        })
      )
      .subscribe((response: string) => {
        console.log('Xóa người dùng thành công:', response);
        this.getUsers(); // Load lại danh sách
        this.errorMessage = ''; // Xóa thông báo lỗi nếu có từ lần trước
      });
  }
}
