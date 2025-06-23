package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.FavoriteRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {
    @Query("SELECT fr FROM FavoriteRecipe fr WHERE fr.user.id = :userId")
    List<FavoriteRecipe> findByUserId(Long userId);

    void deleteByRecipeId(Long recipeId);

    void deleteByUserId(Long userId);
}