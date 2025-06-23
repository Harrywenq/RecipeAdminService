import { Role } from "src/app/models/role";

export interface UserResponse {
    id: number;
    username: string;
    displayName: string;
    dateOfBirth: Date;
    role: Role;
    roleName: String;
}