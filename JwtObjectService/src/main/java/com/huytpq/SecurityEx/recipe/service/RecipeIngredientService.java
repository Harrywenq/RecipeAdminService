package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeIngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeIngredientOutput;

import java.util.List;

public interface RecipeIngredientService {
    List<RecipeIngredientOutput> getList(Long recipeId);

    void create(RecipeIngredientInput input);

    void delete(Long id);
}