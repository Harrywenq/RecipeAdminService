import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/app/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  private apiUrl = `${environment.apiBaseUrl}/api/recipes`;

  constructor(private http: HttpClient) {}

  getRecipes(
    name: string,
    categoryId: number,
    page: number,
    limit: number
  ): Observable<any> {
    let params: { [key: string]: string } = {
      page: page.toString(),
      limit: limit.toString(),
    };
    if (name) {
      params['name'] = name;
    }
    // Luôn gửi category_id, kể cả khi categoryId = 0
    if (categoryId !== undefined && categoryId !== null) {
      params['category_id'] = categoryId.toString();
    }
    return this.http.get<any>(this.apiUrl, { params });
  }

  getDetailRecipe(recipeId: number) {
    return this.http.get(`${environment.apiBaseUrl}/api/recipes/${recipeId}`);
  }
}
