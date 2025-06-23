package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRecipeInput {
    private Long userId;
    private Long recipeId;
}