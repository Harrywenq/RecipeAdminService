import {
    IsString,
    IsNotEmpty,
    IsDate
} from 'class-validator';

export class LoginDto {
    @IsString()
    @IsNotEmpty()
    username: string;

    @IsString()
    @IsNotEmpty()
    password: string;

    @IsNotEmpty()
    roleId: number = 1;

    constructor(data: any) {
        this.username = data.username;
        this.password = data.password;
        this.roleId = data.role_id;
    }
}