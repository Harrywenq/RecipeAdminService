package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepo extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);

    Optional<Ingredient> findById(Long id);
}