package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.FavoriteRecipeInput;
import com.huytpq.SecurityEx.recipe.dto.output.FavoriteRecipeOutput;

import java.util.List;

public interface FavoriteRecipeService {
    List<FavoriteRecipeOutput> getList(Long userId);

    void create(FavoriteRecipeInput input);

    void delete(Long id);
}