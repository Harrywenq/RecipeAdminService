package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeTagInput {
    private Long recipeId;
    private Long tagId;
}