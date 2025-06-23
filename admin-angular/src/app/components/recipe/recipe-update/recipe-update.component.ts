import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserService } from 'src/app/service/user.service';
import { environment } from 'src/app/environments/environment';
import { Category } from 'src/app/models/category';
import { CategoryService } from 'src/app/service/category.service';
import { Router } from '@angular/router';
import { TagOutput } from 'src/app/dtos/tag/tag.dto';

@Component({
  selector: 'app-recipe-update',
  templateUrl: './recipe-update.component.html',
  styleUrls: ['./recipe-update.component.scss']
})
export class RecipeUpdateComponent implements OnInit {
  recipeForm: FormGroup;
  message = '';
  thumbnailFile: File | null = null;
  detailFiles: File[] = [];
  categories: Category[] = [];
  tags: TagOutput[] = [];
  isSubmitting = false;
  readonly MAX_IMAGES = 5;

  thumbnailPreview: string | null = null;
  detailPreviews: string[] = [];

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private userService: UserService,
    private categoryService: CategoryService,
    private router: Router
  ) {
    this.recipeForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      categoryId: ['', Validators.required],
      tagId: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    debugger;
    this.loadCategories();
    this.loadTags();
  }

  loadCategories(): void {
    this.categoryService.getCategories(1, 100).subscribe({
      next: (data: Category[]) => {
        this.categories = data;
      },
      error: (err) => {
        console.error('Lỗi khi tải danh mục:', err);
        this.message = 'Không thể tải danh mục món ăn';
      },
    });
  }

  loadTags(): void {
      const token = localStorage.getItem('access_token') || '';
      let headers = new HttpHeaders();
      if (token) {
        headers = headers.set('Authorization', `Bearer ${token}`);
      }
  
      this.http
        .get<TagOutput[]>(`${environment.apiBaseUrl}/api/tags`, { headers })
        .subscribe({
          next: (data: TagOutput[]) => {
            this.tags = data;
          },
          error: (err) => {
            console.error('Lỗi khi tải danh sách tags:', err);
            this.message = 'Không thể tải danh sách thẻ';
          },
        });
    }

  onThumbnailChange(event: Event) {
    debugger;
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      if (file.type.startsWith('image/') && file.size < 10 * 1024 * 1024) {
        this.thumbnailFile = file;
        this.thumbnailPreview = URL.createObjectURL(file);
      } else {
        this.message =
          'Ảnh đại diện không hợp lệ (chỉ chấp nhận ảnh dưới 10MB).';
        input.value = '';
        this.thumbnailFile = null;
        this.thumbnailPreview = null;
      }
    }
  }

  onFilesChange(event: Event) {
    debugger;
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      this.message = 'Vui lòng chọn ít nhất một ảnh chi tiết!';
      this.detailFiles = [];
      this.detailPreviews = [];
      return;
    }

    if (input.files.length > this.MAX_IMAGES) {
      this.message = `Chỉ được chọn tối đa ${this.MAX_IMAGES} ảnh chi tiết!`;
      input.value = '';
      this.detailFiles = [];
      this.detailPreviews = [];
      return;
    }

    const validFiles = Array.from(input.files).filter(
      (file) => file.type.startsWith('image/') && file.size < 3 * 1024 * 1024
    );

    if (validFiles.length !== input.files.length) {
      this.message =
        'Một số ảnh chi tiết không hợp lệ (chỉ chấp nhận ảnh dưới 3MB).';
      input.value = '';
      this.detailFiles = [];
      this.detailPreviews = [];
    } else {
      this.detailFiles = validFiles;
      this.detailPreviews = validFiles.map((file) => URL.createObjectURL(file));
    }
  }

  submitRecipe() {
    debugger;
    if (this.recipeForm.invalid) {
      this.message = 'Vui lòng điền đầy đủ thông tin hợp lệ!';
      this.recipeForm.markAllAsTouched();
      return;
    }

    const user = this.userService.getUserResponseFromLocalStorage();
    if (!user?.id) {
      this.message = 'Vui lòng đăng nhập lại!';
      return;
    }

    if (!this.thumbnailFile) {
      this.message = 'Vui lòng chọn ảnh đại diện!';
      return;
    }

    this.isSubmitting = true;
    const formData = new FormData();

    const input = {
      ...this.recipeForm.value,
      categoryId: Number(this.recipeForm.value.categoryId),
      userId: Number(user.id),
      tagId: Number(this.recipeForm.value.tagId),
    };

    try {
      const inputJson = JSON.stringify(input);
      console.log('Dữ liệu gửi lên:', inputJson);
      debugger;
      formData.append('input', inputJson);
    } catch (e) {
      this.message = 'Dữ liệu công thức không hợp lệ!';
      this.isSubmitting = false;
      console.error('Lỗi JSON:', e);
      return;
    }

    formData.append('thumbnail', this.thumbnailFile);
    for (let file of this.detailFiles) {
      formData.append('files', file);
    }

    this.http
      .put(`${environment.apiBaseUrl}/api/recipes`, formData)
      .subscribe({
        next: () => {
          this.message = 'Chỉnh sửa công thức thành công!';
          this.isSubmitting = false;
          this.recipeForm.reset();
          this.thumbnailFile = null;
          this.thumbnailPreview = null;
          this.detailFiles = [];
          this.detailPreviews = [];
          (document.getElementById('thumbnail') as HTMLInputElement).value = '';
          (document.getElementById('files') as HTMLInputElement).value = '';
        },
        error: (err) => {
          debugger;
          this.isSubmitting = false;
          console.error('Lỗi API đầy đủ:', err);
          if (err.error?.code) {
            switch (err.error.code) {
              case 9:
                this.message =
                  'Lỗi server: Dữ liệu gửi lên không hợp lệ, vui lòng kiểm tra lại!';
                break;
              case 'RECIPE_NAME_EXISTED':
                this.message = 'Tên công thức đã tồn tại!';
                break;
              case 'RECIPE_CATEGORY_NOT_FOUND':
                this.message = 'Danh mục không hợp lệ!';
                break;
              case 'USER_NOT_FOUND':
                this.message = 'Người dùng không tồn tại!';
                break;
              case 'PAYLOAD_TOO_LARGE':
                this.message =
                  'File quá lớn! Thumbnail tối đa 10MB, ảnh chi tiết tối đa 3MB.';
                break;
              case 'UNSUPPORTED_MEDIA_TYPE':
                this.message = 'File không phải định dạng ảnh!';
                break;
              case 'IMAGE_LIMIT_EXCEEDED':
                this.message = `Chỉ được upload tối đa ${this.MAX_IMAGES} ảnh chi tiết!`;
                break;
              case 'INVALID_IMAGE_URL':
                this.message = 'URL ảnh không hợp lệ!';
                break;
              default:
                this.message = `Lỗi server (code ${err.error.code}): ${
                  err.error.message || 'Không xác định'
                }`;
            }
          } else {
            this.message = err.error || 'Đã xảy ra lỗi khi tạo công thức.';
          }
        },
      });
  }

  goBack() {
    this.router.navigate(['/recipe-management']); 
  }
}
