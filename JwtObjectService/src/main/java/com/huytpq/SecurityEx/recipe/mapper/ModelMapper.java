package com.huytpq.SecurityEx.recipe.mapper;

import com.huytpq.SecurityEx.base.mapper.BaseModelMapper;
import com.huytpq.SecurityEx.recipe.dto.input.*;
import com.huytpq.SecurityEx.recipe.dto.output.PostOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeCategoryOutput;
import com.huytpq.SecurityEx.recipe.dto.output.RecipeOutput;
import com.huytpq.SecurityEx.recipe.dto.output.TagOutput;
import com.huytpq.SecurityEx.recipe.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ModelMapper extends BaseModelMapper {

    // Recipe
    @Mapping(target = "tagId", expression = "java(recipe.getTag() != null ? recipe.getTag().getId() : null)")
    @Mapping(target = "tagName", expression = "java(recipe.getTag() != null ? recipe.getTag().getName() : null)")
    RecipeOutput convertToRecipeOutput(Recipe recipe);

    Recipe createRecipe(RecipeInput input);

    Recipe updateRecipe(@MappingTarget Recipe recipe, RecipeInput input);

    // RecipeCategory
    List<RecipeCategoryOutput> convertToRecipeCategoryOutputs(List<RecipeCategory> recipeCategories);

    RecipeCategoryOutput convertToRecipeCategoryOutput(RecipeCategory recipeCategory);

    RecipeCategory createRecipeCategory(RecipeCategoryInput input);

    RecipeCategory updateRecipeCategory(@MappingTarget RecipeCategory recipeCategory, RecipeCategoryInput input);

    // Post
    PostOutput convertToPostOutput(Post post);

    Post createPost(PostInput input);

    Post updatePost(@MappingTarget Post post, PostInput input);

    // Tag
    List<TagOutput> convertToTagOutputs(List<Tag> tags);

    TagOutput convertToTagOutput(Tag tag);

    Tag createTag(TagInput input);

    Tag updateTag(@MappingTarget Tag tag, TagInput input);

    //user
    User updateUser(@MappingTarget User user, UserUpdateInput input);
}