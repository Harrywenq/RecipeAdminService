// src/app/service/post.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface PostOutput {
  id: number;
  title: string;
  content: string;
  userId: number;
  userName: string;
}

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8090/api/posts';

  constructor(private http: HttpClient) {}

  // Lấy danh sách bài viết (có thể tìm theo title)
  getAllPosts(title?: string): Observable<PostOutput[]> {
    let params = new HttpParams();
    if (title) {
      params = params.set('title', title);
    }
    return this.http.get<PostOutput[]>(this.apiUrl, { params });
  }

  // Xóa bài viết theo ID
  deletePost(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      responseType: 'text', // Để parse đúng kiểu text từ backend
    });
  }
}
