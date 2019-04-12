package com.stevecrossin.whatcanicook.entities;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipe;")
    List<Recipe> getAllRecipes();

    @Query("SELECT Recipe.* FROM recipeingredients\n" +
            "JOIN recipe ON recipeingredients.recipe_name = Recipe.recipe_name\n" +
            " WHERE recipeingredients.recipe_ingredients IN \n" +
            " (SELECT ingredient_name FROM ingredient WHERE ingredient_selected = 1 AND ingredient_excluded = 0) \n" +
            " GROUP BY recipeingredients.recipe_name ORDER BY count(recipeingredients.recipe_name) DESC;")
    List<Recipe> getAllRecipesByCheckedIngredients();

    @Query(" SELECT count(recipe_name) FROM recipeingredients\n" +
            " WHERE recipeingredients.recipe_ingredients IN \n" +
            " (SELECT ingredient_name FROM ingredient WHERE ingredient_selected = 1 AND ingredient_excluded = 0) \n" +
            " AND recipe_name = :name\n" +
            " GROUP BY recipeingredients.recipe_name ORDER BY count(recipe_name) DESC;")
    List<Integer> getNumberOfMissingIngredientsByName(String name);

    @Query(" SELECT recipe_ingredients FROM RecipeIngredients\n" +
            " WHERE recipe_name = :name\n" +
            " AND recipeingredients.recipe_ingredients NOT IN \n" +
            " (SELECT ingredient_name FROM ingredient WHERE ingredient_selected = 1 AND ingredient_excluded = 0) ;")
    List<String> getMissingIngredientsByName(String name);

    @Query("SELECT * FROM recipe WHERE recipe_name = :recipeName;")
    List<Recipe> getRecipesByName(String recipeName);

    @Query("SELECT * FROM recipe WHERE recipe_name IN (:recipeNames);")
    List<Recipe> getRecipesByNames(List<String> recipeNames);

    @Insert
    void addRecipes(ArrayList<Recipe> recipes);

    @Query("DELETE FROM recipe;")
    void deleteAll();

}
