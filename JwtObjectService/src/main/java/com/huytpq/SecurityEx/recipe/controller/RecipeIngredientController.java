package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.RecipeIngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeIngredientOutput;
import com.huytpq.SecurityEx.recipe.service.RecipeIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-ingredients")
public class RecipeIngredientController {

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    @GetMapping
    public ResponseEntity<List<RecipeIngredientOutput>> getList(
            @RequestParam(required = false) Long recipeId) {
        return ResponseEntity.ok(recipeIngredientService.getList(recipeId));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody RecipeIngredientInput input) {
        recipeIngredientService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recipeIngredientService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}