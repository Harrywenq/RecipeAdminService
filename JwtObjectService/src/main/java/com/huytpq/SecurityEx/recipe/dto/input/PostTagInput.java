package com.huytpq.SecurityEx.recipe.dto.input;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTagInput {
    private Long postId;
    private Long tagId;
}