package com.stevecrossin.whatcanicook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.stevecrossin.whatcanicook.R;
import com.stevecrossin.whatcanicook.entities.Recipe;

public class MyRecipesViewHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView recipeName;
    private AppCompatImageView recipeImage;
    AppCompatImageView recipeRemoveButton;

    /**
     * Initialise the fields of each row in the viewholder - in this case the name of the recipe, the image, and the button to remove the recipe.
     */
    MyRecipesViewHolder(@NonNull View itemView) {
        super(itemView);

        recipeName = itemView.findViewById(R.id.recipe_name);
        recipeImage = itemView.findViewById(R.id.recipe_img);
        recipeRemoveButton = itemView.findViewById(R.id.remove);
    }

    /**
     * Binds each row in the view holder to a record in the recipes database and sets text in row to that respective database record
     * Also sets the recipe image in that row based on the drawable resource for that recipe in the database, if it exists.
     */
    @SuppressLint("StaticFieldLeak")
    void bindRow(final Recipe recipe, final Context context) {
        recipeName.setText(recipe.getRecipeName());
        int drawableResourceId = context.getResources().getIdentifier(recipe.getRecipeImage(), "drawable", context.getPackageName());
        recipeImage.setImageResource(drawableResourceId);

    }
}