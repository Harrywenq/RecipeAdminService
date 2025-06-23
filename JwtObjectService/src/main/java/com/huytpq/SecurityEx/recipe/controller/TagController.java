package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.TagInput;
import com.huytpq.SecurityEx.recipe.dto.output.TagOutput;
import com.huytpq.SecurityEx.recipe.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagOutput>> getList(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(tagService.getList(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagOutput> get(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody TagInput input) {
        tagService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody TagInput input) {
        tagService.update(id, input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}