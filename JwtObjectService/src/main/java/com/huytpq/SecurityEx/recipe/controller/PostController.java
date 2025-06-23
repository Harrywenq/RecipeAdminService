package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.PostInput;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;
import com.huytpq.SecurityEx.recipe.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<PostOutput>> getList(
            @RequestParam(required = false) String title) {
        return ResponseEntity.ok(postService.getList(title));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostOutput> get(@PathVariable Long id) {
        return ResponseEntity.ok(postService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody PostInput input) {
        postService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody PostInput input) {
        postService.update(id, input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");

    }
}