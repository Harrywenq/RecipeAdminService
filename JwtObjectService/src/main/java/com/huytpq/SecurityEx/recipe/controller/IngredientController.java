package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.recipe.dto.input.IngredientInput;
import com.huytpq.SecurityEx.recipe.dto.output.IngredientOutput;
import com.huytpq.SecurityEx.recipe.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<List<IngredientOutput>> getList() {
        return ResponseEntity.ok(ingredientService.getList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientOutput> get(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody IngredientInput input) {
        ingredientService.create(input);
        return ResponseEntity.ok("Successful");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestBody IngredientInput input) {
        ingredientService.update(id, input);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}