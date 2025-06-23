import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/client/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/users/register/register.component';
import { DetailRecipeComponent } from './components/recipe/detail-recipe/detail-recipe.component';
import { UserDetailComponent } from './components/users/user-detail/user-detail.component';
import { PostManagementComponent } from './components/posts/post-management/post-management.component';
import { RecipeUpdateComponent } from './components/recipe/recipe-update/recipe-update.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { RecipeCreateComponent } from './components/recipe/recipe-create/recipe-create.component';
import { RecipeManagementComponent } from './components/recipe/recipe-management/recipe-management.component';
import { UserListComponent } from './components/users/user-list/user-list.component';
import { CategoryListComponent } from './components/categories/category-list/category-list.component';
import { CategoryCreateComponent } from './components/categories/category-create/category-create.component';
import { CategoryUpdateComponent } from './components/categories/category-update/category-update.component';
import { TagListComponent } from './components/tags/tag-list/tag-list.component';
import { TagCreateComponent } from './components/tags/tag-create/tag-create.component';
import { TagUpdateComponent } from './components/tags/tag-update/tag-update.component';
import { AdminComponent } from './components/admin/admin.component';
import { AuthGuardFn } from './guards/auth.guard';
import { AdminGuardFn } from './guards/admin.guard';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'admin', component: AdminComponent, canActivate: [AdminGuardFn] },
  { path: 'register', component: RegisterComponent },
  { path: 'recipes/:id', component: DetailRecipeComponent },
  { path: 'user-details', component: UserDetailComponent },
  { path: 'recipe-create', component: RecipeCreateComponent },
  { path: 'post-management', component: PostManagementComponent },
  { path: 'recipe-update', component: RecipeUpdateComponent },
  { path: 'user-update/:id', component: UserUpdateComponent },

  { path: 'recipe-management', component: RecipeManagementComponent },

  { path: 'user-list', component: UserListComponent },
  { path: 'category-list', component: CategoryListComponent },
  { path: 'categories/create', component: CategoryCreateComponent },
  { path: 'categories/edit/:id', component: CategoryUpdateComponent },
  { path: 'tag-list', component: TagListComponent },
  { path: 'tag/create', component: TagCreateComponent },
  { path: 'tag/update/:id', component: TagUpdateComponent },
  
  { path: '**', redirectTo: 'recipe-management' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
