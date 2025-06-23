package com.huytpq.SecurityEx.recipe.dto.output;

import java.util.List;

public class RecipeListOutput {
    private List<RecipeOutput> recipes;
    private int totalPages;

    public RecipeListOutput(List<RecipeOutput> recipes, int totalPages) {
        this.recipes = recipes;
        this.totalPages = totalPages;
    }

    public List<RecipeOutput> getRecipes() {
        return recipes;
    }

    public int getTotalPages() {
        return totalPages;
    }
}