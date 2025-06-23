package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.FavoriteRecipeInput;
import com.huytpq.SecurityEx.recipe.dto.output.FavoriteRecipeOutput;
import com.huytpq.SecurityEx.recipe.entity.FavoriteRecipe;
import com.huytpq.SecurityEx.recipe.entity.Recipe;
import com.huytpq.SecurityEx.recipe.entity.User;
import com.huytpq.SecurityEx.recipe.repo.FavoriteRecipeRepo;
import com.huytpq.SecurityEx.recipe.repo.RecipeRepo;
import com.huytpq.SecurityEx.recipe.repo.UserRepo;
import com.huytpq.SecurityEx.recipe.service.FavoriteRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FavoriteRecipeServiceImpl implements FavoriteRecipeService {

    @Autowired
    private FavoriteRecipeRepo favoriteRecipeRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RecipeRepo recipeRepo;

    @Override
    public List<FavoriteRecipeOutput> getList(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<FavoriteRecipe> favoriteRecipes = favoriteRecipeRepo.findByUserId(userId);
        return favoriteRecipes.stream().map(favoriteRecipe -> {
            FavoriteRecipeOutput output = new FavoriteRecipeOutput();
            output.setId(favoriteRecipe.getId());
            var user = userRepo.findUserById(favoriteRecipe.getUser().getId());
            output.setUserId(user.get().getId());
            output.setUserName(user.get().getUsername());
            var recipe = recipeRepo.findRecipeById(favoriteRecipe.getRecipe().getId());
            output.setRecipeId(recipe.get().getId());
            output.setRecipeName(recipe.get().getName());
            return output;
        }).collect(Collectors.toList());
    }

    @Override
    public void create(FavoriteRecipeInput input) {
        User user = userRepo.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Recipe recipe = recipeRepo.findById(input.getRecipeId())
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe();
        favoriteRecipe.setUser(user);
        favoriteRecipe.setRecipe(recipe);

        favoriteRecipeRepo.save(favoriteRecipe);
    }

    @Override
    public void delete(Long id) {
        FavoriteRecipe favoriteRecipe = favoriteRecipeRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.FAVORITE_RECIPE_NOT_FOUND));
        favoriteRecipeRepo.delete(favoriteRecipe);
    }
}