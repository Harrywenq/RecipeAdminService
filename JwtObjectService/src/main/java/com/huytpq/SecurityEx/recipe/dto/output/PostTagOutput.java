package com.huytpq.SecurityEx.recipe.dto.output;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostTagOutput {
    private Long id;

    private Long postId;
    private String postTitle;
    private String postContent;

    private Long tagId;
    private String tagName;
}