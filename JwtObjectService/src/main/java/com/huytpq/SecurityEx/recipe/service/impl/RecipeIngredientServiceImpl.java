package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeIngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeIngredientOutput;
import com.huytpq.SecurityEx.recipe.entity.Ingredient;
import com.huytpq.SecurityEx.recipe.entity.Recipe;
import com.huytpq.SecurityEx.recipe.entity.RecipeIngredient;
import com.huytpq.SecurityEx.recipe.repo.IngredientRepo;
import com.huytpq.SecurityEx.recipe.repo.RecipeIngredientRepo;
import com.huytpq.SecurityEx.recipe.repo.RecipeRepo;
import com.huytpq.SecurityEx.recipe.service.RecipeIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeIngredientServiceImpl implements RecipeIngredientService {

    @Autowired
    private RecipeIngredientRepo recipeIngredientRepo;

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private IngredientRepo ingredientRepo;

    @Override
    public List<RecipeIngredientOutput> getList(Long recipeId) {
        if (recipeId == null) {
            return Collections.emptyList();
        }
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepo.findByRecipeId(recipeId);
        return recipeIngredients.stream().map(recipeIngredient -> {
            RecipeIngredientOutput output = new RecipeIngredientOutput();
            output.setId(recipeIngredient.getId());
            var recipe = recipeRepo.findRecipeById(recipeIngredient.getRecipe().getId());
            output.setRecipeId(recipe.get().getId());
            output.setRecipeName(recipe.get().getName());
            var ingredient = ingredientRepo.findById(recipeIngredient.getIngredient().getId());
            output.setIngredientId(ingredient.get().getId());
            output.setIngredientName(ingredient.get().getName());
            output.setQuantity(recipeIngredient.getQuantity());
            output.setUnit(recipeIngredient.getUnit());
            return output;
        }).collect(Collectors.toList());
    }

    @Override
    public void create(RecipeIngredientInput input) {
        Recipe recipe = recipeRepo.findById(input.getRecipeId())
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));
        Ingredient ingredient = ingredientRepo.findById(input.getIngredientId())
                .orElseThrow(() -> new BaseException(ErrorCode.INGREDIENT_NOT_FOUND));

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity(input.getQuantity());
        recipeIngredient.setUnit(input.getUnit());

        recipeIngredientRepo.save(recipeIngredient);
    }

    @Override
    public void delete(Long id) {
        RecipeIngredient recipeIngredient = recipeIngredientRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_INGREDIENT_NOT_FOUND));
        recipeIngredientRepo.delete(recipeIngredient);
    }
}