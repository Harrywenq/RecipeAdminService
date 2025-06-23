package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRecipeOutput {
    private Long id;
    private Long userId;
    private String userName;
    private Long recipeId;
    private String recipeName;
}