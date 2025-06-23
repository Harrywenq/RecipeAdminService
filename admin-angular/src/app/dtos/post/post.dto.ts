// src/app/dtos/post/post.dto.ts

export interface PostOutput {
  id: number;
  title: string;
  content: string;
  userId: number;
  userName: string;
  createdAt?: string;
  imageUrl?: string;
  // Thêm các thuộc tính này vào đây
  isEditable?: boolean; // Tùy chọn, vì nó được thêm vào runtime
  isDeletable?: boolean; // Tùy chọn, vì nó được thêm vào runtime
}

export interface PostInput {
  title: string;
  content: string;
  userId: number;
  imageUrl?: string;
}
