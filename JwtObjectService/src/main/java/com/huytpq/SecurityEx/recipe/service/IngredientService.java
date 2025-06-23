package com.huytpq.SecurityEx.recipe.service;

import com.huytpq.SecurityEx.recipe.dto.input.IngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.IngredientOutput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;

import java.util.List;

public interface IngredientService {
    List<IngredientOutput> getList();

    IngredientOutput get(Long id);

    void create(IngredientInput input);

    void update(Long id, IngredientInput input);

    void delete(Long id);
}