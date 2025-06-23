package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeImageRepo extends JpaRepository<RecipeImage, Long> {
    List<RecipeImage> findByRecipeId(Long recipeId);

    void deleteByRecipeId(Long recipeId);

}
