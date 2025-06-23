import { Component, OnInit, ViewChild, TemplateRef, OnDestroy } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PostService } from 'src/app/service/post.service';
import { UserService } from 'src/app/service/user.service';
import { PostOutput, PostInput } from 'src/app/dtos/post/post.dto';
import { UserResponse } from 'src/app/responses/user/user.response';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss']
})
export class PostComponent implements OnInit, OnDestroy {
  posts: PostOutput[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  dialogErrorMessage: string = '';

  @ViewChild('postDialog') postDialog!: TemplateRef<any>;
  isEditMode: boolean = false;
  currentPost: Partial<PostOutput & PostInput> = {};
  isSaving: boolean = false;

  searchKeyword: string = '';

  currentUser: UserResponse | null = null;
  currentUserDisplayName: string = 'Bạn';
  // currentUserAvatarUrl: string | null = null; // Bỏ vì người dùng không có avatar theo backend

  // Không cần Subscription nữa
  // private userSubscription: Subscription | undefined;
  private postsSubscription: Subscription | undefined;

  constructor(
    private postService: PostService,
    private userService: UserService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    // Lấy thông tin người dùng từ localStorage ngay lập tức
    this.currentUser = this.userService.getUserResponseFromLocalStorage();
    console.log('PostComponent: Initial currentUser from localStorage:', this.currentUser);

    if (this.currentUser) {
      this.currentUserDisplayName = this.currentUser.displayName || this.currentUser.username || 'Bạn';
    } else {
      this.currentUserDisplayName = 'Bạn';
    }

    this.loadPosts(); // Tải bài viết lần đầu
  }

  // Tải danh sách bài viết
  loadPosts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    if (this.postsSubscription) {
      this.postsSubscription.unsubscribe();
    }

    this.postsSubscription = this.postService.getAllPosts().subscribe({
      next: (data: PostOutput[]) => {
        this.posts = data.map(post => ({
          ...post,
          // Kiểm tra quyền chỉnh sửa/xóa dựa trên userId của bài viết và currentUser
          isEditable: this.currentUser ? post.userId === this.currentUser.id : false,
          isDeletable: this.currentUser ? post.userId === this.currentUser.id : false,
          createdAt: (post as any).createdAt || new Date().toISOString(),
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading posts:', error);
        this.errorMessage = 'Không thể tải bài viết. Vui lòng thử lại.';
        this.isLoading = false;
      }
    });
  }

  // Tìm kiếm bài viết (giữ nguyên)
  searchPosts(): void {
    if (this.searchKeyword.trim() === '') {
      this.loadPosts();
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';
    this.postService.searchPosts(this.searchKeyword).subscribe({
      next: (data: PostOutput[]) => {
        this.posts = data.map(post => ({
          ...post,
          isEditable: this.currentUser ? post.userId === this.currentUser.id : false,
          isDeletable: this.currentUser ? post.userId === this.currentUser.id : false
        }));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error searching posts:', error);
        this.errorMessage = 'Không thể tìm kiếm bài viết.';
        this.isLoading = false;
      }
    });
  }

  // Mở dialog tạo bài viết mới (giữ nguyên)
  openCreatePostDialog(): void {
    // Đảm bảo cập nhật currentUser ngay trước khi kiểm tra
    this.currentUser = this.userService.getUserResponseFromLocalStorage(); // Cập nhật lại user trước khi kiểm tra
    console.log('Current user before creating post dialog check:', this.currentUser);

    if (!this.currentUser || !this.currentUser.id) {
      alert('Bạn cần đăng nhập để tạo bài viết.'); //
      return;
    }
    this.isEditMode = false;
    this.currentPost = { userId: this.currentUser.id };
    this.dialogErrorMessage = '';
    this.modalService.open(this.postDialog, { centered: true });
  }

  // Mở dialog chỉnh sửa bài viết (giữ nguyên)
  openEditPostDialog(post: PostOutput): void {
    // Đảm bảo cập nhật currentUser ngay trước khi kiểm tra
    this.currentUser = this.userService.getUserResponseFromLocalStorage(); // Cập nhật lại user trước khi kiểm tra

    if (!this.currentUser || post.userId !== this.currentUser.id) {
      alert('Bạn không có quyền sửa bài viết này.');
      return;
    }
    this.isEditMode = true;
    this.currentPost = { ...post };
    this.dialogErrorMessage = '';
    this.modalService.open(this.postDialog, { centered: true });
  }

  // Lưu bài viết (tạo mới hoặc cập nhật) (giữ nguyên)
  savePost(): void {
    // Đảm bảo cập nhật currentUser ngay trước khi kiểm tra và gửi request
    this.currentUser = this.userService.getUserResponseFromLocalStorage(); // Cập nhật lại user trước khi kiểm tra

    if (!this.currentUser || !this.currentUser.id) {
      this.dialogErrorMessage = 'Bạn cần đăng nhập để thực hiện hành động này.';
      return;
    }
    if (!this.currentPost.title || !this.currentPost.content) {
      this.dialogErrorMessage = 'Tiêu đề và nội dung bài viết không được để trống.';
      return;
    }

    this.isSaving = true;
    this.dialogErrorMessage = '';

    const postData: PostInput = {
      title: this.currentPost.title,
      content: this.currentPost.content,
      userId: this.currentUser.id,
    };

    const action = this.isEditMode
      ? this.postService.updatePost(this.currentPost.id!, postData)
      : this.postService.createPost(postData);

    action.subscribe({
      next: () => {
        this.modalService.dismissAll();
        this.loadPosts();
        this.isSaving = false;
      },
      error: (error) => {
        console.error('Lỗi khi lưu bài viết:', error);
        this.dialogErrorMessage = 'Lỗi: ' + (error.error?.message || error.message || 'Không xác định');
        this.isSaving = false;
      }
    });
  }

  // Xác nhận và xóa bài viết (giữ nguyên)
  confirmDeletePost(postId: number): void {
    // Đảm bảo cập nhật currentUser ngay trước khi kiểm tra
    this.currentUser = this.userService.getUserResponseFromLocalStorage(); // Cập nhật lại user trước khi kiểm tra

    if (!this.currentUser || !this.currentUser.id) {
      alert('Bạn cần đăng nhập để xóa bài viết.');
      return;
    }
    const postToDelete = this.posts.find(p => p.id === postId);
    if (!postToDelete || postToDelete.userId !== this.currentUser.id) {
      alert('Bạn không có quyền xóa bài viết này.');
      return;
    }

    if (confirm('Bạn có chắc chắn muốn xóa bài viết này không?')) {
      this.postService.deletePost(postId).subscribe({
        next: () => {
          this.posts = this.posts.filter(p => p.id !== postId);
        },
        error: (error) => {
          console.error('Lỗi khi xóa bài viết:', error);
          this.errorMessage = 'Không thể xóa bài viết: ' + (error.error?.message || error.message || 'Không xác định');
        }
      });
    }
  }

  // Hàm ngOnDestroy không còn cần unsubscribe từ userSubscription
  ngOnDestroy(): void {
    // if (this.userSubscription) { // Dòng này sẽ bị loại bỏ
    //   this.userSubscription.unsubscribe(); // Dòng này sẽ bị loại bỏ
    // } // Dòng này sẽ bị loại bỏ
    if (this.postsSubscription) {
      this.postsSubscription.unsubscribe();
    }
  }
}