import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { Recipe } from 'src/app/models/recipe';
import { RecipeImage } from 'src/app/models/recipe.image';
import { CategoryService } from 'src/app/service/category.service';
import { RecipeService } from 'src/app/service/recipe.service';

@Component({
  selector: 'app-detail-recipe',
  templateUrl: './detail-recipe.component.html',
  styleUrls: ['./detail-recipe.component.scss'],
})
export class DetailRecipeComponent implements OnInit {
  recipe?: Recipe;
  recipeId: number = 0;
  currentImageIndex: number = 0;

  constructor(
    private recipeService: RecipeService,
    private categoryService: CategoryService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe((params) => {
      const idParam = params.get('id');
      if (idParam !== null) {
        this.recipeId = +idParam;
        this.loadRecipeDetail(this.recipeId);
      } else {
        console.error('No recipeId found in URL');
      }
    });
  }

  loadRecipeDetail(recipeId: number) {
    if (!isNaN(recipeId)) {
      this.recipeService.getDetailRecipe(recipeId).subscribe({
        next: (response: any) => {
          if (
            response.recipeImageOutputs &&
            response.recipeImageOutputs.length > 0
          ) {
            response.recipeImageOutputs.forEach((recipeImage: RecipeImage) => {
              recipeImage.imageUrl = `${environment.apiBaseUrl}/api/recipes/images/${recipeImage.imageUrl}`;
            });
          }

          this.recipe = response;
          this.showImage(0);
        },
        complete: () => {},
        error: (error: any) => {
          console.error('Error fetching detail:', error);
        },
      });
    } else {
      console.error('Invalid recipeId:', recipeId);
    }
  }

  showImage(index: number): void {
    if (
      this.recipe &&
      this.recipe.recipeImageOutputs &&
      this.recipe.recipeImageOutputs.length > 0
    ) {
      if (index < 0) {
        index = 0;
      } else if (index >= this.recipe.recipeImageOutputs.length) {
        index = this.recipe.recipeImageOutputs.length - 1;
      }

      this.currentImageIndex = index;
    }
  }

  thumbnailClick(index: number) {
    this.currentImageIndex = index;
  }

  nextImage(): void {
    this.showImage(this.currentImageIndex + 1);
  }

  previousImage(): void {
    this.showImage(this.currentImageIndex - 1);
  }
}
