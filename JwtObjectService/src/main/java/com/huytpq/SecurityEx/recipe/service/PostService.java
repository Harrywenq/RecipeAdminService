package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.PostInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;

import java.util.List;

public interface PostService {
    List<PostOutput> getList(String title);

    PostOutput get(Long id);

    PostOutput create(PostInput input);

    PostOutput update(Long id, PostInput input);

    void delete(Long id);
}