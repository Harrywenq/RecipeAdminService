// src/app/service/post.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { PostOutput, PostInput } from '../dtos/post/post.dto'; // Import DTOs mới

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = `${environment.apiBaseUrl}/api/posts`; // Điều chỉnh URL cho khớp với @RequestMapping("/api/posts")

  constructor(private http: HttpClient) {}

  private getAuthHeaders(): HttpHeaders {
    // Giả sử bạn lưu token trong localStorage hoặc có TokenService
    const token = localStorage.getItem('token'); // Hoặc lấy từ TokenService của bạn
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`, // Thêm Authorization header
    });
  }

  getAllPosts(title?: string): Observable<PostOutput[]> {
    let params: any = {};
    if (title) {
      params.title = title; // Thêm tham số title nếu có
    }
    return this.http.get<PostOutput[]>(this.apiUrl, {
      headers: this.getAuthHeaders(),
      params: params,
    });
  }

  getPostById(id: number): Observable<PostOutput> {
    return this.http.get<PostOutput>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }

  createPost(postInput: PostInput): Observable<any> {
    // Backend trả về String "Successful"
    return this.http.post<any>(this.apiUrl, postInput, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json',
    });
    // responseType: 'text' as 'json' để xử lý khi backend trả về chuỗi thay vì JSON object
  }

  updatePost(id: number, postInput: PostInput): Observable<any> {
    // Backend trả về String "Successful"
    return this.http.put<any>(`${this.apiUrl}/${id}`, postInput, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json',
    });
  }

  deletePost(id: number): Observable<any> {
    // Backend trả về String "Deleted successfully!"
    return this.http.delete<any>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text' as 'json',
    });
  }

  // searchPosts bây giờ chính là getAllPosts với tham số title
  searchPosts(keyword: string): Observable<PostOutput[]> {
    return this.getAllPosts(keyword);
  }
}
