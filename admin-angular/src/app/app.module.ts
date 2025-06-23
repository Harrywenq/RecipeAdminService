import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HomeComponent } from './components/client/home/home.component';
import { HeaderComponent } from './components/client/header/header.component';
import { FooterComponent } from './components/client/footer/footer.component';
import { DetailRecipeComponent } from './components/recipe/detail-recipe/detail-recipe.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/users/register/register.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app/app.component';
import { NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';
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
// import { AdminLayoutComponent } from './components/client/admin-layout/admin-layout.component';

@NgModule({
  declarations: [
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    DetailRecipeComponent,
    LoginComponent,
    RegisterComponent,
    AppComponent,
    UserDetailComponent,
    RecipeCreateComponent,
    PostManagementComponent,
    RecipeUpdateComponent,
    UserUpdateComponent,
    RecipeManagementComponent,
    UserListComponent,
    CategoryListComponent,
    CategoryCreateComponent,
    CategoryUpdateComponent,
    TagListComponent,
    TagCreateComponent,
    TagUpdateComponent,
    AdminComponent,
    // AdminLayoutComponent,
  ],
  imports: [
    ReactiveFormsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    NgbPopoverModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [
    AppComponent,
    // HomeComponent
    // DetailRecipeComponent
    // LoginComponent
    // RegisterComponent
  ],
})
export class AppModule {}
