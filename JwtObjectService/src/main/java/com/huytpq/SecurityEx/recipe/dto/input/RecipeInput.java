package com.huytpq.SecurityEx.recipe.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeInput {
    private String name;

    private String description;

    private String thumbnail;

    private Long userId;

    private Long categoryId;

    private Long tagId;

}
