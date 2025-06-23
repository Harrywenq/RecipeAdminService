package com.huytpq.SecurityEx.recipe.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huytpq.SecurityEx.base.exception.BaseException;
import com.huytpq.SecurityEx.base.exception.ErrorCode;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeImageInput;
import com.huytpq.SecurityEx.recipe.dto.input.RecipeInput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeImageOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeOutput;
import com.huytpq.SecurityEx.recipe.entity.*;
import com.huytpq.SecurityEx.recipe.mapper.ModelMapper;
import com.huytpq.SecurityEx.recipe.repo.*;
import com.huytpq.SecurityEx.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepo recipeRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RecipeImageRepo recipeImageRepo;
    @Autowired
    private RecipeCategoryRepo recipeCategoryRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RecipeTagRepo recipeTagRepo;
    @Autowired
    private TagRepo tagRepo;
    @Autowired
    private PostTagRepo postTagRepo;
    @Autowired
    private RecipeIngredientRepo recipeIngredientRepo;
    @Autowired
    private FavoriteRecipeRepo favoriteRecipeRepo;

    @Override
    public Page<RecipeOutput> getList(String name, Long categoryId, Pageable pageable) {
        Page<Recipe> recipePage = recipeRepo.findByCondition(name, categoryId, pageable);

        return recipePage.map(recipe -> {
            RecipeOutput recipeOutput = new RecipeOutput();
            recipeOutput.setId(recipe.getId());
            recipeOutput.setName(recipe.getName());
            recipeOutput.setDescription(recipe.getDescription());
            recipeOutput.setThumbnail(recipe.getThumbnail());

            var user = userRepo.findUserById(recipe.getUser().getId());
            recipeOutput.setUserId(user.get().getId());
            recipeOutput.setUserName(user.get().getUsername());

            var cate = recipeCategoryRepo.findCategoryById(recipe.getRecipeCategory().getId());
            recipeOutput.setCategoryId(cate.get().getId());
            recipeOutput.setCategoryName(cate.get().getName());

//            List<RecipeTag> recipeTags = recipeTagRepo.findByRecipeId(recipe.getId());
//            recipe.setTag(recipeTags.get(0).getTag());

            if (recipeOutput.getId() != null) {
                var recipeImages = recipeImageRepo.findByRecipeId(recipeOutput.getId());
                if (recipeImages != null && !recipeImages.isEmpty()) {
                    List<RecipeImageOutput> recipeImageOutputs = new ArrayList<>();
                    for (RecipeImage recipeImage : recipeImages) {
                        RecipeImageOutput recipeImageOutput = new RecipeImageOutput();
                        recipeImageOutput.setImageUrlId(recipeImage.getId());
                        recipeImageOutput.setImageUrl(recipeImage.getImageUrl());
                        recipeImageOutputs.add(recipeImageOutput);
                    }
                    recipeOutput.setRecipeImageOutputs(recipeImageOutputs);
                }
            }

            return recipeOutput;
        });
    }

    @Override
    public RecipeOutput get(Long id) {
        var recipe =
                recipeRepo
                        .findById(id)
                        .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));
//        var out = modelMapper.convertToRecipeOutput(recipe);
//        return out;

        RecipeOutput output = new RecipeOutput();
        output.setId(recipe.getId());
        output.setName(recipe.getName());
        output.setDescription(recipe.getDescription());
        output.setThumbnail(recipe.getThumbnail());
        var user = userRepo.findUserById(recipe.getUser().getId());
        output.setUserId(user.get().getId());
        output.setUserName(user.get().getUsername());
        var cate = recipeCategoryRepo.findCategoryById(recipe.getRecipeCategory().getId());
        output.setCategoryId(cate.get().getId());
        output.setCategoryName(cate.get().getName());

//        List<RecipeTag> recipeTags = recipeTagRepo.findByRecipe(recipe);
//        recipe.setTag(recipeTags.get(0).getTag());

        if (output.getId() != null) {

            var recipeImages = recipeImageRepo.findByRecipeId(output.getId());

            if (recipeImages != null && !recipeImages.isEmpty()) {
                List<RecipeImageOutput> recipeImageOutputs = new ArrayList<>();

                for (RecipeImage recipeImage : recipeImages) {
                    RecipeImageOutput recipeImageOutput = new RecipeImageOutput();
                    recipeImageOutput.setImageUrlId(recipeImage.getId());
                    recipeImageOutput.setImageUrl(recipeImage.getImageUrl());
                    recipeImageOutputs.add(recipeImageOutput);
                }
                output.setRecipeImageOutputs(recipeImageOutputs);
            } else {
                output.setRecipeImageOutputs(Collections.emptyList());
            }
        }
        return output;
    }

    @Override
    @Transactional
    public RecipeOutput create(RecipeInput input) {
        if (recipeRepo.findByName(input.getName()).isPresent()) {
            throw new BaseException(ErrorCode.RECIPE_NAME_EXISTED);
        }
        Recipe recipe = modelMapper.createRecipe(input);
        RecipeCategory category = recipeCategoryRepo.findById(input.getCategoryId())
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_CATEGORY_NOT_FOUND));
        User user = userRepo.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        recipe.setRecipeCategory(category);
        recipe.setUser(user);
        recipe = recipeRepo.save(recipe);

        Tag tag = tagRepo.findTagById(input.getTagId())
                .orElseThrow(() -> new BaseException(ErrorCode.TAG_NOT_FOUND));

        RecipeTag recipeTag = new RecipeTag();
        recipeTag.setRecipe(recipe);
        recipeTag.setTag(tag);
        recipeTagRepo.save(recipeTag);

        return modelMapper.convertToRecipeOutput(recipe);
    }

    @Override
    public Map<String, Object> update(Long id, String inputJson, MultipartFile thumbnail, List<MultipartFile> files) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RecipeInput input = objectMapper.readValue(inputJson, RecipeInput.class);

            if (input == null) {
                throw new BaseException(ErrorCode.INVALID_INPUT);
            }
            var record =
                    recipeRepo
                            .findById(id)
                            .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));
            if (recipeRepo
                    .findByNameAndIdNot(input.getName(), id)
                    .isPresent()) {
                throw new BaseException(ErrorCode.RECIPE_NAME_EXISTED);
            }
            var updateRecipe = modelMapper.updateRecipe(record, input);
            recipeRepo.save(updateRecipe);

            String thumbnailUrl = null;
            if (thumbnail != null) {
                if (thumbnail.getSize() > 10 * 1024 * 1024) {
                    throw new BaseException(ErrorCode.PAYLOAD_TOO_LARGE);
                }
                String contentType = thumbnail.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BaseException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
                }
                thumbnailUrl = storeFile(thumbnail);
                input.setThumbnail(thumbnailUrl);
            }

            //xóa ảnh cũ và thêm ảnh mới
            files = files == null ? new ArrayList<>() : files;
            if (files.size() > RecipeImage.MAXIMUM_IMAGES_PER_RECIPE) {
                throw new BaseException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
            }

            var recipeImages = recipeImageRepo.findByRecipeId(record.getId());
            if (recipeImages != null && !recipeImages.isEmpty()) {
                recipeImageRepo.deleteAll(recipeImages);
            }

            List<RecipeImage> newRecipeImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if (file.getSize() > 3 * 1024 * 1024) {
                    throw new BaseException(ErrorCode.PAYLOAD_TOO_LARGE);
                }
                String imageUrl = storeFile(file);
                RecipeImage recipeImage = createRecipeImage(
                        record.getId(),
                        RecipeImageInput.builder().imageUrl(imageUrl).build()
                );
                newRecipeImages.add(recipeImage);
            }

            if (!newRecipeImages.isEmpty()) {
                recipeImageRepo.saveAll(newRecipeImages);
            }
            recipeRepo.save(record);

            Map<String, Object> response = new HashMap<>();
            response.put("gift", record);
            response.put("images", recipeImages);
            return response;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Recipe recipe = recipeRepo.findById(id)
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));
        recipeTagRepo.deleteByRecipeId(id);
        recipeImageRepo.deleteByRecipeId(id);
        recipeIngredientRepo.deleteByRecipeId(id);
        favoriteRecipeRepo.deleteByRecipeId(id);

        recipeRepo.delete(recipe);
    }

    @Override
    public void createRecipeWithImages(String inputJson, MultipartFile thumbnail, List<MultipartFile> files) {
        try {
            // Parse chuỗi JSON thành GiftInput
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            RecipeInput input = objectMapper.readValue(inputJson, RecipeInput.class);

            // Xử lý thumbnail
            String thumbnailUrl = null;
            if (thumbnail != null) {
                if (thumbnail.getSize() > 10 * 1024 * 1024) {
                    throw new BaseException(ErrorCode.PAYLOAD_TOO_LARGE);
                }
                String contentType = thumbnail.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BaseException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
                }
                thumbnailUrl = storeFile(thumbnail);
                input.setThumbnail(thumbnailUrl);
            }

            // Tạo Gift
            RecipeOutput createdRecipe = create(input);

            // Xử lý danh sách ảnh imageUrl
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > RecipeImage.MAXIMUM_IMAGES_PER_RECIPE) {
                throw new BaseException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
            }

            List<RecipeImage> recipeImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                if (file.getSize() > 3 * 1024 * 1024) {
                    throw new BaseException(ErrorCode.PAYLOAD_TOO_LARGE);
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BaseException(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
                }
                String fileName = storeFile(file);
                RecipeImage recipeImage = createRecipeImage(
                        createdRecipe.getId(),
                        RecipeImageInput.builder()
                                .imageUrl(fileName)
                                .build()
                );
                recipeImages.add(recipeImage);
            }

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("gift", createdRecipe);
            response.put("images", recipeImages);
        } catch (IOException e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RecipeImage createRecipeImage(Long recipeId, RecipeImageInput input) {
        Recipe existingRecipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new BaseException(ErrorCode.RECIPE_NOT_FOUND));

        if (input.getImageUrl() == null || input.getImageUrl().isEmpty()) {
            throw new BaseException(ErrorCode.INVALID_IMAGE_URL);
        }

        int size = recipeImageRepo.findByRecipeId(recipeId).size();
        if (size >= RecipeImage.MAXIMUM_IMAGES_PER_RECIPE) {
            throw new BaseException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        RecipeImage newRecipeImage = RecipeImage.builder()
                .recipe(existingRecipe)
                .imageUrl(input.getImageUrl())
                .build();

        return recipeImageRepo.save(newRecipeImage);
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + fileName;
        Path uploadDir = Paths.get("F:/Work/DATN/RecipeService/JwtObjectService/upload");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        file.getInputStream();
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
