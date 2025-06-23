package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.FavoriteRecipeInput;
import com.huytpq.SecurityEx.recipe.dto.output.FavoriteRecipeOutput;
import com.huytpq.SecurityEx.recipe.service.FavoriteRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-recipes")
public class FavoriteRecipeController {

    @Autowired
    private FavoriteRecipeService favoriteRecipeService;

    @GetMapping
    public ResponseEntity<List<FavoriteRecipeOutput>> getList(
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(favoriteRecipeService.getList(userId));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody FavoriteRecipeInput input) {
        favoriteRecipeService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        favoriteRecipeService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}