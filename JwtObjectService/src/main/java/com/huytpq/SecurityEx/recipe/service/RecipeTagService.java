package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeTagOutput;

import java.util.List;

public interface RecipeTagService {
    List<RecipeTagOutput> getList(Long recipeId);

    void create(RecipeTagInput input);

    void delete(Long id);
}