package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCategoryOutput {
    private Long id;
    private String name;
    private String description;
}
