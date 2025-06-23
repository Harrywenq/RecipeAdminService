package com.huytpq.SecurityEx.recipe.repo;

import com.huytpq.SecurityEx.recipe.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepo extends JpaRepository<PostTag, Long> {
    @Query("SELECT pt FROM PostTag pt WHERE pt.post.id = :postId")
    List<PostTag> findByPostId(Long postId);

    void deleteByPostId(Long id);
}