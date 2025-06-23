import { IsString, IsNotEmpty, IsDate } from 'class-validator';

export class RegisterDto {
  @IsString()
  @IsNotEmpty()
  username: string;

  @IsString()
  @IsNotEmpty()
  password: string;

  @IsString()
  @IsNotEmpty()
  retypePassword: string;

  @IsDate()
  dateOfBirth: Date;

  @IsString()
  @IsNotEmpty()
  displayName: string;

  role: string;

  constructor(data: any) {
    this.username = data.username;
    this.password = data.password;
    this.retypePassword = data.retypePassword;
    this.dateOfBirth = data.dateOfBirth;
    this.displayName = data.display_name;
    this.role = data.role || 'ADMIN'; 
  }
}
