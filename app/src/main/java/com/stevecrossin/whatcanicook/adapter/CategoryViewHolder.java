package com.stevecrossin.whatcanicook.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.stevecrossin.whatcanicook.R;
import com.stevecrossin.whatcanicook.entities.Ingredient;

/**
 * ViewHolder, which dictates and sets the contents of the view
 */
class CategoryViewHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView categoryName;
    private AppCompatImageView categoryImage;

    /**
     * Initialise the fields of each row in the viewholder - in this case the name of the category, and the icon for that category.
     */
    CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.category_name);
        categoryImage = itemView.findViewById(R.id.category_img);
    }

    /**
     * Binds each row in the view holder to a record in the ingredients database and sets text in row to that database record.
     * Sets the image for that category based on the drawable resource for that category as per the database
     **/
    void bindRow(Ingredient category, Context context) {
        categoryName.setText(category.getIngredientCategory());
        int drawableResourceId = context.getResources().getIdentifier(category.getCategoryIconName(), "drawable", context.getPackageName());
        categoryImage.setImageResource(drawableResourceId);

    }

    /**
     * Getter methods - gets category name and categoryimage and returns the result
     */
    AppCompatTextView getCategoryName() {
        return categoryName;
    }

    AppCompatImageView getCategoryImage() {
        return categoryImage;
    }
}