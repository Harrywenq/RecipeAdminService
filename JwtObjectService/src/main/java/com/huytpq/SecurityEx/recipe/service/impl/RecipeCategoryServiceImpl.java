package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeCategoryInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeCategoryOutput;
import com.huytpq.SecurityEx.recipe.entity.RecipeCategory;
import com.huytpq.SecurityEx.recipe.mapper.ModelMapper;
import com.huytpq.SecurityEx.recipe.repo.RecipeCategoryRepo;
import com.huytpq.SecurityEx.recipe.service.RecipeCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeCategoryServiceImpl implements RecipeCategoryService {

    @Autowired
    private RecipeCategoryRepo recipeCategoryRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RecipeCategoryOutput> getList(String name) {
        List<RecipeCategory> recipeCategories = recipeCategoryRepo.findByCondition(name);
        return modelMapper.convertToRecipeCategoryOutputs(recipeCategories);
    }

    @Override
    public RecipeCategoryOutput get(Long id) {
        var recipeCategory =
                recipeCategoryRepo
                        .findById(id)
                        .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_CATEGORY_NOT_FOUND));
        var out = modelMapper.convertToRecipeCategoryOutput(recipeCategory);
        return out;
    }

    @Override
    public RecipeCategoryOutput create(RecipeCategoryInput input) {
        recipeCategoryRepo.findByName(input.getName());
        if (recipeCategoryRepo
                .findByName(input.getName())
                .isPresent()) {
            throw new BaseException(ErrorCode.RECIPE_CATEGORY_NAME_EXISTED);
        }
        RecipeCategory recipeCategory = modelMapper.createRecipeCategory(input);
        recipeCategory = recipeCategoryRepo.save(recipeCategory);
        return modelMapper.convertToRecipeCategoryOutput(recipeCategory);
    }

    @Override
    public RecipeCategoryOutput update(Long id, RecipeCategoryInput input) {
        var record =
                recipeCategoryRepo
                        .findById(id)
                        .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_CATEGORY_NOT_FOUND));
        if (recipeCategoryRepo
                .findByNameAndIdNot(input.getName(), id)
                .isPresent()) {
            throw new BaseException(ErrorCode.RECIPE_CATEGORY_NAME_EXISTED);
        }
        var updateRecipeCategory = modelMapper.updateRecipeCategory(record, input);
        recipeCategoryRepo.save(updateRecipeCategory);
        return null;
    }

    @Override
    public void delete(Long id) {
        RecipeCategory recipeCategory = recipeCategoryRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_CATEGORY_NOT_FOUND));
        recipeCategoryRepo.delete(recipeCategory);
    }
}
