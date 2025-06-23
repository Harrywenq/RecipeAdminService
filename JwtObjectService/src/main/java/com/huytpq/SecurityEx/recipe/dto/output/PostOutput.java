package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostOutput {
    private Long id;

    private String title;

    private String content;

    private Long userId;

    private String userName;
}