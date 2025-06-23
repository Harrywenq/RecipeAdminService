package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.TagInput;
import com.huytpq.SecurityEx.recipe.dto.output.TagOutput;

import java.util.List;

public interface TagService {
    List<TagOutput> getList(String name);

    TagOutput get(Long id);

    TagOutput create(TagInput input);

    TagOutput update(Long id, TagInput input);

    void delete(Long id);
}