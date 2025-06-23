import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/client/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/users/register/register.component';
import { DetailRecipeComponent } from './components/recipe/detail-recipe/detail-recipe.component';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { UserDetailComponent } from './components/users/user-detail/user-detail.component';
import { FavoriteRecipesComponent } from './components/recipe/favorite-recipes/favorite-recipes.component';
import { PostComponent } from './components/posts/post/post.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { CategoryListComponent } from './components/categories/category-list/category-list.component';
import { AdminComponent } from './components/admin/admin.component';
import { AuthGuardFn } from './guards/auth.guard';
import { AdminGuardFn } from './guards/admin.guard';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'admin', component: AdminComponent, canActivate: [AdminGuardFn] },
  { path: 'register', component: RegisterComponent },
  { path: 'recipes/:id', component: DetailRecipeComponent },
  { path: 'chatbot', component: ChatbotComponent },
  { path: 'user-details', component: UserDetailComponent },
  { path: 'favorite-recipes', component: FavoriteRecipesComponent },
  { path: 'posts', component: PostComponent },
  { path: 'user-update/:id', component: UserUpdateComponent },
  { path: 'category-list', component: CategoryListComponent },
  { path: '**', redirectTo: 'home' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
