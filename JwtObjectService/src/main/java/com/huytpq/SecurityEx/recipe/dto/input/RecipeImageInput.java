package com.huytpq.SecurityEx.recipe.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeImageInput {
    private String imageUrl;

    private Long recipeId;

}
