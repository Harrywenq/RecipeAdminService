package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    @Query(
            value =
                    "SELECT p "
                            + "FROM Post p "
                            + "WHERE (:title IS NULL OR LOWER(p.title) LIKE CONCAT('%', LOWER(:title), '%')) "
                            + "ORDER BY p.updatedAt DESC"
    )
    List<Post> findByCondition(String title);

    Optional<Post> findByTitle(String title);

    Optional<Post> findByTitleAndIdNot(String title, Long id);

    Optional<Post> findPostById(Long id);

    void deleteByUserId(Long userId);

    List<Post> findByUserId(Long userId);
}