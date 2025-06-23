package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.IngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.IngredientOutput;
import com.huytpq.SecurityEx.recipe.entity.Ingredient;
import com.huytpq.SecurityEx.recipe.repo.IngredientRepo;
import com.huytpq.SecurityEx.recipe.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepo ingredientRepo;

    @Override
    public List<IngredientOutput> getList() {
        List<Ingredient> ingredients = ingredientRepo.findAll();
        return ingredients.stream().map(ingredient -> {
            IngredientOutput output = new IngredientOutput();
            output.setId(ingredient.getId());
            output.setName(ingredient.getName());
            return output;
        }).collect(Collectors.toList());
    }

    @Override
    public void create(IngredientInput input) {
        ingredientRepo.findByName(input.getName())
                .ifPresent(ingredient -> {
                    throw new BaseException(ErrorCode.INGREDIENT_NAME_EXISTED);
                });

        Ingredient ingredient = new Ingredient();
        ingredient.setName(input.getName());

        ingredientRepo.save(ingredient);
    }

    @Override
    public IngredientOutput get(Long id) {
        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.INGREDIENT_NOT_FOUND));
        IngredientOutput output = new IngredientOutput();
        output.setId(ingredient.getId());
        output.setName(ingredient.getName());
        return output;
    }

    @Override
    public void update(Long id, IngredientInput input) {
        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.INGREDIENT_NOT_FOUND));

        ingredientRepo.findByName(input.getName())
                .ifPresent(existingIngredient -> {
                    if (!existingIngredient.getId().equals(id)) {
                        throw new BaseException(ErrorCode.INGREDIENT_NAME_EXISTED);
                    }
                });

        ingredient.setName(input.getName());
        ingredientRepo.save(ingredient);
    }

    @Override
    public void delete(Long id) {
        Ingredient ingredient = ingredientRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.INGREDIENT_NOT_FOUND));
        ingredientRepo.delete(ingredient);
    }
}