package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientOutput {
    private Long id;
    private Long recipeId;
    private String recipeName;
    private Long ingredientId;
    private String ingredientName;
    private Long quantity;
    private String unit;
}