package com.stevecrossin.whatcanicook.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stevecrossin.whatcanicook.R;
import com.stevecrossin.whatcanicook.adapter.MyIngredientViewAdapter;
import com.stevecrossin.whatcanicook.entities.Ingredient;
import com.stevecrossin.whatcanicook.roomdatabase.AppDataRepo;

import java.util.ArrayList;

public class MyIngredients extends AppCompatActivity {
    private AppDataRepo repository;
    MyIngredientViewAdapter myIngredientViewAdapter;
    private static final String TAG = "MyIngredients";

    /**
     * Scence initialization
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myingredients);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        repository = new AppDataRepo(this);

        initRecyclerItems();
    }

    /**
     * This will do the setup step for our recycle view:
     * 1. find the recycle view in the layout with id my_ingredients_list
     * 2. set the layout manager
     * 3. set up event listener for recycleview on row clicked [DEBUGGING PURPOSE]
     * 4. set adapter for the recycle view
     * 5. finall, call loadingredients method to populate data
     */
    private void initRecyclerItems() {
        RecyclerView myIngredientsList = findViewById(R.id.my_ingredients_list);
        myIngredientsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        myIngredientViewAdapter = new MyIngredientViewAdapter(new ArrayList<Ingredient>(), new MyIngredientViewAdapter.rowClickedListener() {
            @Override
            public void onRowClicked(Ingredient ingredient) {
            }
        });
        myIngredientsList.setAdapter(myIngredientViewAdapter);
        loadIngredients();
    }

    /**
     * Core function to load ingredients and update recycleview adapter.
     * This function performs an async task in the background to get a list of ingredient from the database (getAllCheckedIngredients)
     * It will then store those ingredients in an ArrayList and return the list.
     */
    @SuppressLint("StaticFieldLeak")
    public void loadIngredients() {
        new AsyncTask<Void, Void, ArrayList<Ingredient>>() {
            @Override
            protected ArrayList<Ingredient> doInBackground(Void... voids) {
                return new ArrayList<>(repository.getAllCheckedIngredients());
            }

            @Override
            protected void onPostExecute(ArrayList<Ingredient> ingredients) {
                super.onPostExecute(ingredients);
                myIngredientViewAdapter.updateIngredients(ingredients);
            }
        }.execute();
    }

    public void findRecipes(View view) {
        Intent intent = new Intent(MyIngredients.this, Recipes.class);
        startActivity(intent);
    }

    //Navigate to Main Activity
    public void navigateHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
