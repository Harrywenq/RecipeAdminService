import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { Category } from 'src/app/models/category';
import { Recipe } from 'src/app/models/recipe';
import { CategoryService } from 'src/app/service/category.service';
import { RecipeService } from 'src/app/service/recipe.service';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from '@angular/common/http';
import { UserService } from 'src/app/service/user.service';

@Component({
  selector: 'app-recipe-management',
  templateUrl: './recipe-management.component.html',
  styleUrls: ['./recipe-management.component.scss'],
})
export class RecipeManagementComponent implements OnInit {
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
    this.token = localStorage.getItem('access_token') || '';
    const userResponseString = localStorage.getItem('user_response');
    if (userResponseString) {
      try {
        const userResponse = JSON.parse(userResponseString);
        this.userId = userResponse.id;
      } catch (e) {
        console.error('Error parsing user_response from localStorage', e);
        this.errorMessage =
          'Lỗi đọc thông tin người dùng. Vui lòng đăng nhập lại.';
      }
    } else {
      this.errorMessage =
        'Không tìm thấy thông tin người dùng. Vui lòng đăng nhập.';
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
          this.errorMessage = '';
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

  onCreateNewRecipe() {
    this.router.navigate(['/recipe-create']);
  }

  onUpdate(recipeId: number) {
    this.router.navigate(['/recipe-update', recipeId]);
  }

  deleteRecipe(recipeId: number) {
    if (!confirm('Bạn có chắc chắn muốn xóa công thức này?')) {
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .delete(`${environment.apiBaseUrl}/api/recipes/${recipeId}`, {
        headers,
        responseType: 'text',
      })
      .subscribe({
        next: (response: string) => {
          this.errorMessage = response || 'Xóa công thức thành công!'; // Sử dụng phản hồi text
          this.getRecipes(
            this.name,
            this.selectedCategoryId,
            this.currentPage,
            this.itemsPerPage
          );
          setTimeout(() => {
            this.errorMessage = '';
          }, 3000);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error deleting recipe:', error);
          if (
            error.error &&
            typeof error.error === 'object' &&
            error.error.message
          ) {
            this.errorMessage = `Lỗi khi xóa công thức: ${error.error.message}`;
          } else if (error.status === 400 || error.status === 500) {
            this.errorMessage =
              'Lỗi khi xóa công thức: Dữ liệu liên quan chưa được xóa.';
          } else {
            this.errorMessage = `Lỗi khi xóa công thức: ${
              error.statusText || 'Không thể kết nối đến server'
            }`;
          }
          if (error.status === 401) {
            this.errorMessage =
              'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
            this.goToLogin();
          }
        },
      });
  }

  goToLogin() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('user_response');
    this.router.navigate(['/login']);
  }
}
