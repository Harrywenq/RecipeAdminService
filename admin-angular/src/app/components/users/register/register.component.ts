import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { RegisterDto } from '../../../dtos/user/register.dto';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm!: NgForm;
  //khai bao cac bien tuong ung voi truong du lieu in form
  username: string;
  password: string;
  retypePassword: string;
  displayName: string;
  isAccepted: boolean;
  dateOfBirth: Date;

  constructor(private router: Router, private userService: UserService) {
    this.username = '';
    this.password = '';
    this.retypePassword = '';
    this.displayName = '';
    this.isAccepted = true;
    this.dateOfBirth = new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 14);
  }
  onUsernameChange() {
    console.log(`User typed: ${this.username}`);
  }

  register() {
    const message =
      `username: ${this.username}` +
      `password: ${this.password}` +
      `retypePassword: ${this.retypePassword}` +
      `fullName: ${this.displayName}` +
      `isAccepted: ${this.isAccepted}` +
      `dateOfBirth: ${this.dateOfBirth}`;
    const userRole = 'USER';
    // alert(message);
    debugger;

    const registerDto: RegisterDto = {
      username: this.username,
      password: this.password,
      retypePassword: this.retypePassword,
      dateOfBirth: this.dateOfBirth,
      displayName: this.displayName,
      role: userRole,
    };

    this.userService.register(registerDto).subscribe({
      next: (response: any) => {
        debugger;
        this.router.navigate(['/login']);
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        alert(`Đăng ký không thành công:, error: ${error.error}`);
      },
    });
  }

  checkPasswordsMatch() {
    if (this.password !== this.retypePassword) {
      this.registerForm.form.controls['retypePassword'].setErrors({
        passwordMismatch: true,
      });
    } else {
      this.registerForm.form.controls['retypePassword'].setErrors(null);
    }
  }

  checkAge() {
    if (this.dateOfBirth) {
      const today = new Date();
      const birthDate = new Date(this.dateOfBirth);
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      if (
        monthDiff < 0 ||
        (monthDiff == 0 && today.getDate() < birthDate.getDate())
      ) {
        age--;
      }
      if (age < 14) {
        this.registerForm.form.controls['dateOfBirth'].setErrors({
          invalidAge: true,
        });
      } else {
        this.registerForm.form.controls['dateOfBirth'].setErrors(null);
      }
    }
  }
}
