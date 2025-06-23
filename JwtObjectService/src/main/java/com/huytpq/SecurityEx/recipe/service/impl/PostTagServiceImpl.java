package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.PostTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostTagOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeTagOutput;
import com.huytpq.SecurityEx.recipe.entity.Post;
import com.huytpq.SecurityEx.recipe.entity.PostTag;
import com.huytpq.SecurityEx.recipe.entity.RecipeTag;
import com.huytpq.SecurityEx.recipe.entity.Tag;
import com.huytpq.SecurityEx.recipe.repo.PostRepo;
import com.huytpq.SecurityEx.recipe.repo.PostTagRepo;
import com.huytpq.SecurityEx.recipe.repo.TagRepo;
import com.huytpq.SecurityEx.recipe.service.PostTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostTagServiceImpl implements PostTagService {

    @Autowired
    private PostTagRepo postTagRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private TagRepo tagRepo;

    @Override
    public List<PostTagOutput> getList(Long postId) {
        List<PostTag> postTags = postTagRepo.findByPostId(postId);
        List<PostTagOutput> postTagOutputs = postTags.stream().map(postTag -> {
            PostTagOutput output = new PostTagOutput();
            output.setId(postTag.getId());
            var post = postRepo.findPostById(postTag.getPost().getId());
            output.setPostId(post.get().getId());
            output.setPostTitle(post.get().getTitle());
            output.setPostContent(post.get().getContent());
            var tag = tagRepo.findTagById(postTag.getTag().getId());
            output.setTagId(tag.get().getId());
            output.setTagName(tag.get().getName());
            return output;
        }).toList();

        return postTagOutputs;
    }

    @Override
    public void create(PostTagInput input) {
        Post post = postRepo.findById(input.getPostId())
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        Tag tag = tagRepo.findById(input.getTagId())
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));

        PostTag postTag = new PostTag();
        postTag.setPost(post);
        postTag.setTag(tag);

        postTagRepo.save(postTag);
    }

    @Override
    public void delete(Long id) {
        PostTag postTag = postTagRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_TAG_NOT_FOUND));
        postTagRepo.delete(postTag);
    }
}