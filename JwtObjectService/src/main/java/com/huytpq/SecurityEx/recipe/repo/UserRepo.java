package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.Post;
import com.huytpq.SecurityEx.recipe.entity.Recipe;
import com.huytpq.SecurityEx.recipe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);

    Optional<User> findByUsernameAndIdNot(String username, Long id);

    @Query(
            value =
                    "SELECT u " +
                            "FROM User u " +
                            "WHERE (:displayName IS NULL OR LOWER(u.displayName) LIKE CONCAT('%', LOWER(:displayName), '%')) " +
                            "ORDER BY u.createdAt DESC"
    )
    List<User> findByCondition(String displayName);
}

