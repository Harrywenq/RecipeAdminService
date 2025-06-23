package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeTagRepo extends JpaRepository<RecipeTag, Long> {
    @Query("SELECT rt FROM RecipeTag rt WHERE rt.recipe.id = :recipeId")
    List<RecipeTag> findByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);

    List<RecipeTag> findByRecipe(Recipe recipe);

}