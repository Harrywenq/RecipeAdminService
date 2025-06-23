package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostInput {
    private String title;

    private String content;

    private Long userId;
}