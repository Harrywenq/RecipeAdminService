package com.huytpq.SecurityEx.recipe.controller;

import com.huytpq.SecurityEx.base.util.PaginationUtil;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeListOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeOutput;
import com.huytpq.SecurityEx.recipe.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public ResponseEntity<RecipeListOutput> getList(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PaginationUtil.createPageRequest(page, limit);
        Page<RecipeOutput> recipePage = recipeService.getList(name, categoryId, pageRequest);

        int totalPages = recipePage.getTotalPages();
        List<RecipeOutput> recipes = recipePage.getContent();

        return ResponseEntity.ok()
                .body(new RecipeListOutput(recipes, totalPages));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeOutput> get(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.get(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(
            @RequestParam("input") String inputJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        try {
            recipeService.createRecipeWithImages(inputJson, thumbnail, files);
            return ResponseEntity.ok("Successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("upload/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestPart("input") String inputJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        recipeService.update(id, inputJson, thumbnail, files);
        return ResponseEntity.ok("Successful");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.ok("Deleted successfully!");
    }
}

