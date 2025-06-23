package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepo extends JpaRepository<RecipeIngredient, Long> {
    @Query("SELECT ri FROM RecipeIngredient ri WHERE ri.recipe.id = :recipeId")
    List<RecipeIngredient> findByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);
}