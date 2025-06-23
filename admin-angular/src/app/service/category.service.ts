import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/app/environments/environment';
import { Category } from '../models/category';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = `${environment.apiBaseUrl}/api/recipe-categories`;

  constructor(private http: HttpClient) {}

  getCategories(page: number, limit: number): Observable<Category[]> {
    const params = {
      page: page.toString(),
      limit: limit.toString(),
    };
    return this.http.get<Category[]>(this.apiUrl, { params });
  }
}
