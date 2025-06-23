package com.huytpq.SecurityEx.recipe.service.impl;

import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.PostInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;
import com.huytpq.SecurityEx.recipe.entity.Post;
import com.huytpq.SecurityEx.recipe.entity.User;
import com.huytpq.SecurityEx.recipe.mapper.ModelMapper;
import com.huytpq.SecurityEx.recipe.repo.PostRepo;
import com.huytpq.SecurityEx.recipe.repo.PostTagRepo;
import com.huytpq.SecurityEx.recipe.repo.RecipeImageRepo;
import com.huytpq.SecurityEx.recipe.repo.UserRepo;
import com.huytpq.SecurityEx.recipe.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PostTagRepo postTagRepo;
    private RecipeImageRepo recipeImageRepo;

    @Override
    public List<PostOutput> getList(String title) {
        List<Post> posts = postRepo.findByCondition(title);
        return posts.stream().map(post -> {
            PostOutput output = new PostOutput();
            output.setId(post.getId());
            output.setTitle(post.getTitle());
            output.setContent(post.getContent());
            var user = userRepo.findUserById(post.getUser().getId());
            output.setUserId(user.get().getId());
            output.setUserName(user.get().getUsername());
            return output;
        }).collect(Collectors.toList());
    }

    @Override
    public PostOutput get(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        PostOutput output = new PostOutput();
        output.setId(post.getId());
        output.setTitle(post.getTitle());
        output.setContent(post.getContent());
        var user = userRepo.findUserById(post.getUser().getId());
        output.setUserId(user.get().getId());
        output.setUserName(user.get().getUsername());
        return output;
    }

    @Override
    public PostOutput create(PostInput input) {
        if (postRepo.findByTitle(input.getTitle()).isPresent()) {
            throw new BaseException(ErrorCode.POST_TITLE_EXISTED);
        }
        User user = userRepo.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Post post = modelMapper.createPost(input);
        post.setUser(user);
        post = postRepo.save(post);
        return modelMapper.convertToPostOutput(post);
    }

    @Override
    public PostOutput update(Long id, PostInput input) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        if (postRepo.findByTitleAndIdNot(input.getTitle(), id).isPresent()) {
            throw new BaseException(ErrorCode.POST_TITLE_EXISTED);
        }
        Post updatedPost = modelMapper.updatePost(post, input);
        postRepo.save(updatedPost);
        return null;
    }

    @Override
    public void delete(Long id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
        postTagRepo.findByPostId(id);
        postRepo.delete(post);
    }
}