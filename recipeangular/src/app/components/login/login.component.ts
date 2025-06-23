import { Component, OnInit, ViewChild } from '@angular/core';
import { LoginDto } from '../../dtos/user/login.dto';
import { UserService } from '../../service/user.service';
import { LoginResponse } from '../../responses/user/login.response';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { TokenService } from 'src/app/service/token.service';
import { RoleService } from 'src/app/service/role.service';
import { Role } from 'src/app/models/role';
import { UserResponse } from 'src/app/responses/user/user.response';
import { timestamp } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  @ViewChild('loginForm') loginForm!: NgForm;

  username: string = '';
  password: string = '';
  errorMessage: string = ''; // Thêm biến này để lưu thông báo lỗi

  roles: Role[] = [];
  rememberMe: boolean = true;
  selectedRole: Role | undefined;
  userResponse?: UserResponse;

  onUsernameChange() {
    console.log(`User typed: ${this.username}`);
    this.errorMessage = ''; // Xóa thông báo lỗi khi người dùng thay đổi username
  }

  constructor(
    private router: Router,
    private userService: UserService,
    private tokenService: TokenService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    //call api getListRole
    debugger;
    this.roleService.getRoles().subscribe({
      next: (roles: Role[]) => {
        debugger;
        this.roles = roles;
        this.selectedRole = roles.length > 0 ? roles[0] : undefined;
      },
      error: (error: any) => {
        debugger;
        console.error('Error getting roles:', error);
      },
    });
  }

  login() {
    this.errorMessage = ''; // Xóa thông báo lỗi cũ trước khi thử đăng nhập
    const message = `username: ${this.username}` + `password: ${this.password}`;
    // alert(message);
    debugger;

    const loginDto: LoginDto = {
      username: this.username,
      password: this.password,
      roleId: this.selectedRole?.id ?? 1,
    };

    this.userService.login(loginDto).subscribe({
      next: (response: LoginResponse) => {
        debugger;
        const { token } = response;
        if (this.rememberMe) {
          this.tokenService.setToken(token);
          debugger;
          this.userService.getUserDetail(token).subscribe({
            next: (response: any) => {
              debugger;
              this.userResponse = {
                ...response,
                dateOfBirth: new Date(response.dateOfBirth),
              };
              this.userService.saveUserResponseToLocalStorage(
                this.userResponse
              );
              if (this.userResponse?.roleName == 'ADMIN') {
                this.router.navigate(['admin']);
              } else if (this.userResponse?.roleName == 'USER') {
                this.router.navigate(['home']);
              }
            },
            complete: () => {
              debugger;
            },
            error: (error: any) => {
              debugger;
              // alert(error.error.message); // Có thể ẩn alert này nếu bạn muốn chỉ hiển thị dưới dạng lỗi HTML
              this.errorMessage =
                'Đăng nhập không thành công, tài khoản hoặc mật khẩu sai.'; // Cập nhật thông báo lỗi
            },
          });
        }
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        // alert(error?.error?.message); // Có thể ẩn alert này nếu bạn muốn chỉ hiển thị dưới dạng lỗi HTML
        this.errorMessage =
          'Đăng nhập không thành công, tài khoản hoặc mật khẩu sai.'; // Cập nhật thông báo lỗi
      },
    });
  }
}
