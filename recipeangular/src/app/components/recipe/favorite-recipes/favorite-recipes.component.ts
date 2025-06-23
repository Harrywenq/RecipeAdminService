import { Component, OnInit } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { UserService } from 'src/app/service/user.service';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-favorite-recipes',
  templateUrl: './favorite-recipes.component.html',
  styleUrls: ['./favorite-recipes.component.scss'],
})
export class FavoriteRecipesComponent implements OnInit {
  favoriteRecipes: any[] = [];
  token: string = '';
  userId: number | null = null;
  errorMessage: string = '';
  successMessage: string = ''; // THÊM BIẾN NÀY
  loading: boolean = true;

  constructor(
    private http: HttpClient,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loadInitialData();
  }

  loadInitialData() {
    this.loading = true;
    this.errorMessage = ''; // Xóa lỗi cũ
    this.successMessage = ''; // Xóa thông báo thành công cũ khi tải lại dữ liệu
    this.favoriteRecipes = [];

    this.token = localStorage.getItem('access_token') || '';
    if (!this.token) {
      this.errorMessage =
        'Bạn chưa đăng nhập hoặc phiên đã hết hạn. Vui lòng đăng nhập.';
      this.loading = false;
      return;
    }

    const userResponse = this.userService.getUserResponseFromLocalStorage();
    if (userResponse && userResponse.id) {
      this.userId = Number(userResponse.id);
      this.fetchFavoriteRecipes();
    } else {
      this.errorMessage =
        'Không tìm thấy thông tin người dùng. Vui lòng thử lại.';
      this.loading = false;
    }
  }

  fetchFavoriteRecipes() {
    if (!this.userId) {
      this.errorMessage = 'Không có userId để lấy danh sách yêu thích.';
      this.loading = false;
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .get<any[]>(
        `${environment.apiBaseUrl}/api/favorite-recipes?userId=${this.userId}`,
        { headers }
      )
      .pipe(
        finalize(() => (this.loading = false)),
        catchError((error: HttpErrorResponse) => {
          console.error('Lỗi khi lấy danh sách món ăn yêu thích:', error);
          let displayMessage = 'Lỗi khi lấy danh sách món ăn yêu thích.';

          if (error.status === 401) {
            displayMessage =
              'Phiên đăng nhập của bạn đã hết hạn. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else if (error.error && typeof error.error === 'string') {
            displayMessage = error.error;
          } else if (
            error.error &&
            typeof error.error === 'object' &&
            error.error.message
          ) {
            displayMessage = error.error.message;
          } else if (error.error) {
            displayMessage = JSON.stringify(error.error);
          } else if (error.statusText) {
            displayMessage = `Lỗi: ${error.status} - ${error.statusText}`;
          }
          this.errorMessage = displayMessage;
          this.successMessage = ''; // Đảm bảo không hiển thị thông báo thành công khi có lỗi
          return throwError(() => error);
        })
      )
      .subscribe({
        next: (response) => {
          this.favoriteRecipes = response;
          this.errorMessage = ''; // Xóa thông báo lỗi nếu fetch thành công
        },
      });
  }

  viewRecipe(recipeId: number) {
    this.router.navigate(['/recipes', recipeId]);
  }

  removeFavorite(favoriteId: number) {
    if (!this.token) {
      this.errorMessage = 'Bạn chưa đăng nhập. Vui lòng thử lại.';
      this.successMessage = ''; // Đảm bảo không hiển thị thông báo thành công nếu chưa đăng nhập
      return;
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.token}`,
    });

    this.http
      .delete(`${environment.apiBaseUrl}/api/favorite-recipes/${favoriteId}`, {
        headers,
        responseType: 'text',
      })
      .pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Error removing favorite recipe:', error);
          let displayMessage = 'Đã có lỗi xảy ra khi xóa món yêu thích.';
          if (error.status === 401) {
            displayMessage =
              'Phiên đăng nhập của bạn đã hết hạn. Vui lòng đăng nhập lại.';
            this.router.navigate(['/login']);
          } else if (error.error && typeof error.error === 'string') {
            displayMessage = error.error;
          } else if (
            error.error &&
            typeof error.error === 'object' &&
            error.error.message
          ) {
            displayMessage = error.error.message;
          } else if (error.error) {
            displayMessage = JSON.stringify(error.error);
          } else if (error.statusText) {
            displayMessage = `Lỗi: ${error.status} - ${error.statusText}`;
          }
          this.errorMessage = displayMessage;
          this.successMessage = ''; // Đảm bảo không hiển thị thông báo thành công khi có lỗi
          return throwError(() => error);
        })
      )
      .subscribe({
        next: () => {
          // Cập nhật danh sách favoriteRecipes sau khi xóa thành công
          this.favoriteRecipes = this.favoriteRecipes.filter(
            (recipe) => recipe.id !== favoriteId
          );
          this.errorMessage = ''; // Xóa thông báo lỗi nếu thành công
          this.successMessage = 'Đã xóa món yêu thích thành công!'; // Gán thông báo thành công
          // Đặt timeout để xóa thông báo thành công sau 2 giây
          setTimeout(() => {
            this.successMessage = '';
          }, 2000);
        },
      });
  }

  retry() {
    this.loadInitialData();
  }
}
