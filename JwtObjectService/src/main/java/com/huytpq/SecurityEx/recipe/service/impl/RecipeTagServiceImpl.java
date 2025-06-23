package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeTagOutput;
import com.huytpq.SecurityEx.recipe.entity.Recipe;
import com.huytpq.SecurityEx.recipe.entity.RecipeTag;
import com.huytpq.SecurityEx.recipe.entity.Tag;
import com.huytpq.SecurityEx.recipe.repo.RecipeRepo;
import com.huytpq.SecurityEx.recipe.repo.RecipeTagRepo;
import com.huytpq.SecurityEx.recipe.repo.TagRepo;
import com.huytpq.SecurityEx.recipe.service.RecipeTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecipeTagServiceImpl implements RecipeTagService {

    @Autowired
    private RecipeTagRepo recipeTagRepo;

    @Autowired
    private RecipeRepo recipeRepo;

    @Autowired
    private TagRepo tagRepo;

    @Override
    public List<RecipeTagOutput> getList(Long recipeId) {
        if (recipeId == null) {
            return Collections.emptyList();
        }
        List<RecipeTag> recipeTags = recipeTagRepo.findByRecipeId(recipeId);
        List<RecipeTagOutput> recipeTagOutputs = recipeTags.stream().map(recipeTag -> {
            RecipeTagOutput output = new RecipeTagOutput();
            output.setId(recipeTag.getId());
            var recipe = recipeRepo.findRecipeById(recipeTag.getRecipe().getId());
            output.setRecipeId(recipe.get().getId());
            output.setRecipeName(recipe.get().getName());
            var tag = tagRepo.findTagById(recipeTag.getTag().getId());
            output.setTagId(tag.get().getId());
            output.setTagName(tag.get().getName());
            return output;
        }).collect(Collectors.toList());

        return recipeTagOutputs;
    }

    @Override
    public void create(RecipeTagInput input) {
        Recipe recipe = recipeRepo.findById(input.getRecipeId())
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));
        Tag tag = tagRepo.findById(input.getTagId())
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));

        RecipeTag recipeTag = new RecipeTag();
        recipeTag.setRecipe(recipe);
        recipeTag.setTag(tag);

        recipeTagRepo.save(recipeTag);
    }

    @Override
    public void delete(Long id) {
        RecipeTag recipeTag = recipeTagRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_TAG_NOT_FOUND));
        recipeTagRepo.delete(recipeTag);
    }
}