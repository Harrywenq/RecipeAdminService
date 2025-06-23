package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeTagOutput {
    private Long id;
    private Long recipeId;
    private String recipeName;
    private Long tagId;
    private String tagName;

}