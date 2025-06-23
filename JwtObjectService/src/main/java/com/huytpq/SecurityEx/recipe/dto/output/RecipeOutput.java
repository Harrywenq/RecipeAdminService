package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeOutput {
    private Long id;

    private String name;

    private String description;

    private String thumbnail;

    private Long userId;

    private String userName;

    private Long categoryId;

    private String categoryName;

    private Long tagId;

    private String tagName;

    private List<RecipeImageOutput> recipeImageOutputs;

}
