package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientInput {
    private Long recipeId;
    private Long ingredientId;
    private Long quantity;
    private String unit;
}