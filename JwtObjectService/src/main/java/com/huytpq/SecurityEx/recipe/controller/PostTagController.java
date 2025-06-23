package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.PostTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostTagOutput;
import com.huytpq.SecurityEx.recipe.service.PostTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-tags")
public class PostTagController {

    @Autowired
    private PostTagService postTagService;

    @GetMapping
    public ResponseEntity<List<PostTagOutput>> getList(
            @RequestParam(required = false) Long postId) {
        return ResponseEntity.ok(postTagService.getList(postId));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody PostTagInput input) {
        postTagService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postTagService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}