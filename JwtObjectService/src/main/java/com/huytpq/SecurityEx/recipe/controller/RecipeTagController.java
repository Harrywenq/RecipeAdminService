package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeTagInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeTagOutput;
import com.huytpq.SecurityEx.recipe.service.RecipeTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-tags")
public class RecipeTagController {

    @Autowired
    private RecipeTagService recipeTagService;

    @GetMapping
    public ResponseEntity<List<RecipeTagOutput>> getList(
            @RequestParam(required = false) Long recipeId) {
        return ResponseEntity.ok(recipeTagService.getList(recipeId));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody RecipeTagInput input) {
        recipeTagService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recipeTagService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}