package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepo extends JpaRepository<Tag, Long> {
    @Query(
            value =
                    "SELECT t "
                            + "FROM Tag t "
                            + "WHERE (:name IS NULL OR t.name = :name) "
                            + "ORDER BY t.createdAt DESC"
    )
    List<Tag> findByCondition(String name);

    Optional<Tag> findByName(String name);

    Optional<Tag> findByNameAndIdNot(String name, Long id);

    Optional<Tag> findTagById(Long id);
}