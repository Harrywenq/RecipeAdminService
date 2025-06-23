package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeCategoryRepo extends JpaRepository<RecipeCategory, Long> {
    @Query(
            value =
                    "SELECT rc "
                            + "FROM RecipeCategory rc "
                            + "WHERE (:name IS NULL "
                            + "OR (:name IS NULL OR rc.name = :name)) "
                            + "ORDER BY rc.createdAt DESC"
    )
    List<RecipeCategory> findByCondition(String name);

    Optional<RecipeCategory> findByName(String name);

    Optional<RecipeCategory> findByNameAndIdNot(String name, Long id);

    Optional<RecipeCategory> findCategoryById(Long id);
}
