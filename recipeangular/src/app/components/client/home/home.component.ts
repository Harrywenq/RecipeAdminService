import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { Category } from 'src/app/models/category';
import { Recipe } from 'src/app/models/recipe';
import { CategoryService } from 'src/app/service/category.service';
import { RecipeService } from 'src/app/service/recipe.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserService } from 'src/app/service/user.service'; 

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  recipes: Recipe[] = [];
  categories: Category[] = [];
  selectedCategoryId: number = 0;
  currentPage: number = 1;
  itemsPerPage: number = 12;
  totalPages: number = 0;
  visiblePages: number[] = [];
  name: string = '';
  errorMessage: string = '';
  token: string = '';
  userId: number | null = null;

  constructor(
    private router: Router,
    private recipeService: RecipeService,
    private categoryService: CategoryService,
    private http: HttpClient,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.token = localStorage.getItem('access_token') || ''; //get token addToFavorites, not required

    // Lấy userId từ userResponse trong localStorage
    const userResponse = this.userService.getUserResponseFromLocalStorage();
    if (userResponse && userResponse.id) {
      this.userId = Number(userResponse.id);
    }

    this.getRecipes(
      this.name,
      this.selectedCategoryId,
      this.currentPage,
      this.itemsPerPage
    );
    this.getCategories(1, 100);
  }

  getCategories(page: number, limit: number) {
    this.categoryService.getCategories(page, limit).subscribe({
      next: (categories: Category[]) => {
        this.categories = categories;
      },
      error: (error: any) => {
        console.error('Error fetching categories:', error);
        this.errorMessage =
          'Lỗi khi lấy danh sách danh mục: ' +
          (error.statusText || 'Không thể kết nối đến server');
      },
    });
  }

  searchRecipes() {
    this.currentPage = 1;
    this.getRecipes(
      this.name,
      this.selectedCategoryId,
      this.currentPage,
      this.itemsPerPage
    );
  }

  getRecipes(
    name: string,
    selectedCategoryId: number,
    page: number,
    limit: number
  ) {
    this.recipeService
      .getRecipes(name, selectedCategoryId, page, limit)
      .subscribe({
        next: (response: any) => {
          response.recipes.forEach((recipe: Recipe) => {
            recipe.url = `${environment.apiBaseUrl}/api/recipes/images/${recipe.thumbnail}`;
          });
          this.recipes = response.recipes;
          this.totalPages = response.totalPages;
          this.visiblePages = this.generateVisiblePageArray(
            this.currentPage,
            this.totalPages
          );
        },
        error: (error: any) => {
          console.error('Error fetching recipes:', error);
          this.errorMessage =
            'Lỗi khi lấy danh sách món ăn: ' +
            (error.statusText || 'Không thể kết nối đến server');
        },
      });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.getRecipes(
      this.name,
      this.selectedCategoryId,
      this.currentPage,
      this.itemsPerPage
    );
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1)
      .fill(0)
      .map((_, index) => startPage + index);
  }

  onRecipeClick(recipeId: number) {
    this.router.navigate(['/recipes', recipeId]);
  }

  addToFavorites(recipeId: number) {
    if (!this.token) {
      this.errorMessage = 'Vui lòng đăng nhập để thêm vào yêu thích.';
      return;
    }

    if (!this.userId) {
      this.errorMessage = 'Không tìm thấy userId. Vui lòng đăng nhập lại.';
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    const body = { userId: this.userId, recipeId };

    this.http
      .post(`${environment.apiBaseUrl}/api/favorite-recipes`, body, {
        headers,
        observe: 'response',
      })
      .subscribe({
        next: (response: any) => {
          if (response.status === 200 || response.status === 201) {
            this.errorMessage = 'Đã thêm vào danh sách yêu thích!';
            setTimeout(() => {
              this.errorMessage = '';
            }, 2000);
          } else {
            this.errorMessage = 'Lỗi không xác định khi thêm vào yêu thích.';
          }
        },
        error: (error: any) => {
          console.error('Error adding to favorites:', error);
          this.errorMessage =
            'Đã thêm vào danh sách yêu thích! ' +
            (error.statusText || 'Không thể kết nối đến server');
        },
      });
  }
}
