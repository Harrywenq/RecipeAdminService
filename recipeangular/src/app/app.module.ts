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
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { UserDetailComponent } from './components/users/user-detail/user-detail.component';
import { FavoriteRecipesComponent } from './components/recipe/favorite-recipes/favorite-recipes.component';
import { PostComponent } from './components/posts/post/post.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { CategoryListComponent } from './components/categories/category-list/category-list.component';
import { AdminComponent } from './components/admin/admin.component';

@NgModule({
  declarations: [
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    DetailRecipeComponent,
    LoginComponent,
    RegisterComponent,
    AppComponent,
    ChatbotComponent,
    UserDetailComponent,
    FavoriteRecipesComponent,
    PostComponent,
    UserUpdateComponent,
    CategoryListComponent,
    AdminComponent,
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
