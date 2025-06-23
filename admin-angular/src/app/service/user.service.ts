import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { RegisterDto } from '../dtos/user/register.dto';
import { LoginDto } from '../dtos/user/login.dto';
import { environment } from '../environments/environment';
import { UserResponse } from '../responses/user/user.response';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiRegister = `${environment.apiBaseUrl}/register`;
  private apiLogin = `${environment.apiBaseUrl}/login`;
  private apiUserDetail = `${environment.apiBaseUrl}/details`;

  private apiConfig = {
    headers: this.createHeaders(),
  };

  constructor(private http: HttpClient) {}

  private createHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

  register(registerDto: RegisterDto): Observable<any> {
    return this.http.post(this.apiRegister, registerDto, this.apiConfig);
  }

  login(loginDto: LoginDto): Observable<any> {
    return this.http.post(this.apiLogin, loginDto, this.apiConfig);
  }

  getUserDetail(token: string) {
    return this.http.post(this.apiUserDetail, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      }),
    });
  }

  saveUserResponseToLocalStorage(userResponse?: UserResponse) {
    try {
      debugger;
      if (userResponse == null || !userResponse) {
        return;
      }
      // Convert the userResponse object to a JSON string
      const userResponseJSON = JSON.stringify(userResponse);
      // Save the JSON string to local storage with a key (e.g., "userResponse")
      localStorage.setItem('user', userResponseJSON);

      console.log('User response saved to local storage.');
    } catch (error) {
      console.error('Error saving user response to local storage:', error);
    }
  }

  getUserResponseFromLocalStorage() {
    try {
      // Retrieve the JSON string from local storage using the key
      const userResponseJSON = localStorage.getItem('user');
      if (userResponseJSON == null || userResponseJSON == undefined) {
        return null;
      }
      // Parse the JSON string back to an object
      const userResponse = JSON.parse(userResponseJSON!);

      console.log('User response retrieved from local storage.');
      return userResponse;
    } catch (error) {
      console.error(
        'Error retrieving user response from local storage:',
        error
      );
      return null; // Return null or handle the error as needed
    }
  }

  removetUserFromLocalStorage():void {
    try {
      // remove user data from local storage using the key
      localStorage.removeItem('user');
      console.log('User data removed from local storage.');
    } catch (error) {
      console.error(
        'Error removing user data from local storage:',
        error
      );
    }
  }
}
