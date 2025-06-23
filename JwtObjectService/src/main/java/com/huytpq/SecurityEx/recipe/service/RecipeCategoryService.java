package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeCategoryInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeCategoryOutput;

import java.util.List;

public interface RecipeCategoryService {
    List<RecipeCategoryOutput> getList(String name);
    RecipeCategoryOutput get(Long id);
    RecipeCategoryOutput create(RecipeCategoryInput input);
    RecipeCategoryOutput update(Long id, RecipeCategoryInput input);
    void delete(Long id);
}
