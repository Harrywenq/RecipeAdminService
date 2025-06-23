import { RecipeImage } from './recipe.image';

export interface Recipe {
  id: number;
  name: string;
  description: string;
  thumbnail: string;
  userId: number;
  userName: string;
  categoryId: number;
  categoryName: string;
  url: string;
  recipeImageOutputs?: RecipeImage[];
}
