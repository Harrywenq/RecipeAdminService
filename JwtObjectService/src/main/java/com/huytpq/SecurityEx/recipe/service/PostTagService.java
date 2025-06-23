package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.PostTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostTagOutput;

import java.util.List;

public interface PostTagService {
    List<PostTagOutput> getList(Long postId);

    void create(PostTagInput input);

    void delete(Long id);
}