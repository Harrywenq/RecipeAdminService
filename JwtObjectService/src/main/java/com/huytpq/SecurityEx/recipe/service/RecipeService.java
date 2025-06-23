package com.huytpq.SecurityEx.recipe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeImageInput;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeOutput;
import com.huytpq.SecurityEx.recipe.entity.RecipeImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    Page<RecipeOutput> getList(String name, Long categoryId, Pageable pageable);

    RecipeOutput get(Long id);

    RecipeOutput create(RecipeInput input);

//    RecipeOutput update(Long id, RecipeInput input);

    Map<String, Object> update(Long id, String inputJson, MultipartFile thumbnail, List<MultipartFile> files) throws JsonProcessingException;

    void delete(Long id);

    void createRecipeWithImages(String inputJson, MultipartFile thumbnail, List<MultipartFile> files);


    RecipeImage createRecipeImage(Long recipeId, RecipeImageInput input);
}
