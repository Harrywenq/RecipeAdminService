// src/app/components/post-management/post-management.component.ts

import { Component, OnInit } from '@angular/core';
import { PostService } from 'src/app/service/post.service';
import { UserService } from 'src/app/service/user.service';
import { UserResponse } from 'src/app/responses/user/user.response';

interface PostOutput {
  id: number;
  title: string;
  content: string;
  userId: number;
  userName: string;
}

@Component({
  selector: 'app-post-management',
  templateUrl: './post-management.component.html',
  styleUrls: ['./post-management.component.scss'],
})
export class PostManagementComponent implements OnInit {
  posts: PostOutput[] = [];
  searchTitle: string = '';
  errorMessage: string = '';
  currentUser: UserResponse | null = null;
  private readonly ADMIN_ROLE_ID = 1;

  constructor(
    private postService: PostService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.userService.getUserResponseFromLocalStorage();
    this.loadPosts();
  }

  isAdmin(): boolean {
    return this.currentUser?.role?.id === this.ADMIN_ROLE_ID;
  }

  loadPosts(): void {
    this.errorMessage = '';
    this.postService.getAllPosts(this.searchTitle).subscribe({
      next: (data) => {
        this.posts = data;
      },
      error: (err) => {
        this.errorMessage =
          err.error?.message || 'Đã xảy ra lỗi khi tải danh sách bài viết.';
        console.error('Lỗi khi tải bài viết:', err);
      },
    });
  }

  searchPosts(): void {
    this.loadPosts();
  }

  deletePost(id: number): void {
    this.currentUser = this.userService.getUserResponseFromLocalStorage();

    if (!this.currentUser || !this.currentUser.id) {
      alert('Bạn cần đăng nhập để xóa bài viết.');
      return;
    }

    if (confirm('Bạn có chắc chắn muốn xóa bài viết này không?')) {
      this.postService.deletePost(id).subscribe({
        next: () => {
          alert('Xóa bài viết thành công!');
          this.loadPosts();
        },
        error: (err) => {
          this.errorMessage =
            err.error?.message || 'Đã xảy ra lỗi khi xóa bài viết.';
          console.error('Lỗi khi xóa bài viết:', err);
        },
      });
    }
  }
}
