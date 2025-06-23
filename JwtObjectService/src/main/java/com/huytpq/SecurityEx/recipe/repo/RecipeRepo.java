package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    @Query(
            value =
                    "SELECT r "
                            + "FROM Recipe r "
                            + "WHERE (:name IS NULL OR LOWER(r.name) LIKE CONCAT('%', LOWER(:name), '%')) "
                            + "AND (:categoryId IS NULL OR :categoryId = 0 OR r.recipeCategory.id = :categoryId) "
                            + "ORDER BY r.createdAt DESC"
    )
    Page<Recipe> findByCondition(String name, Long categoryId, Pageable pageable);

    Optional<Recipe> findByName(String name);

    Optional<Recipe> findByNameAndIdNot(String name, Long id);

    Optional<Recipe> findRecipeById(Long id);

    String name(String name);

    void deleteByUserId(Long userId);

    List<Recipe> findByUserId(Long userId);
}
