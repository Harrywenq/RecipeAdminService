package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeCategoryInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeCategoryOutput;
import com.huytpq.SecurityEx.recipe.service.RecipeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-categories")
public class RecipeCategoryController {
    @Autowired
    private RecipeCategoryService recipeCategoryService;

    @GetMapping
    public ResponseEntity<List<RecipeCategoryOutput>> getList(
            @RequestParam(required = false)
            String name) {
        return ResponseEntity.ok(recipeCategoryService.getList(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeCategoryOutput> get(@PathVariable Long id) {
        return ResponseEntity.ok(recipeCategoryService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody RecipeCategoryInput input) {
        recipeCategoryService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody RecipeCategoryInput input) {
        recipeCategoryService.update(id, input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recipeCategoryService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}
