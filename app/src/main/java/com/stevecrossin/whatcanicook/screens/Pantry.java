package com.stevecrossin.whatcanicook.screens;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stevecrossin.whatcanicook.R;
import com.stevecrossin.whatcanicook.adapter.PantryAutocompleteAdapter;
import com.stevecrossin.whatcanicook.adapter.PantryRecycleViewAdapter;
import com.stevecrossin.whatcanicook.entities.Ingredient;
import com.stevecrossin.whatcanicook.roomdatabase.AppDataRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pantry is the class which is run when the user clicks on "My Pantry" from the MainActivity. It will show the user the ingredients they
 * have saved as being in their pantry, allow them to add, and remove additional ingredients to their pantry. It is mainly designed to act like a shopping list
 * so the user knows what they have at home when they are shopping.
 */
public class Pantry extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private RecyclerView recyclerView;

    /**
     * On creation of the activity, perform these functions.
     * Set the current view as the activity_pantry XML and load the UI elements in that XML file into that view.
     * Load Google Ads for the activity and send an adRequest to load an ad.
     * Call initView, getPantryIngredient and loadPantry methods.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.loadAd(adRequest);
        initView();
        getPantryIngredient();
        loadPantry();
    }

    /**
     * This is method is responsible for setting the OnClick listener for the Pantry autocompleteTextView.
     * The OnClick listener has multiple elements, to handle clearing users ingredients from the pantry and deleting individual user ingredients,
     * depending on whether the end user clicks the "Clear Ingredients" button or an individual ingredient row.
     *
     * <p>
     * It also initialises the view by finding the elements that should be displayed in the view, specifically the autocomplete textview and the recycler view, also sets the adapter for the
     * recycler view
     * <p>
     * When an onClick method is triggered, an async task is called in the background which creates a new instance of AppDataRepo
     * and calls the methods to clear the ingredient from the pantry database. Once this is completed the RecyclerView will be updated.
     */
    private void initView() {
        autoCompleteTextView = findViewById(R.id.pantryIngredientsSearchBox);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteTextView.setText(null);
                Ingredient ingredient = (Ingredient) parent.getItemAtPosition(position);
                addPantryIngredientToDB(ingredient);
            }
        });

        recyclerView = findViewById(R.id.pantryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PantryRecycleViewAdapter(new ArrayList<Ingredient>()));

        Button clearAll = findViewById(R.id.pantryClearIngredients);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        AppDataRepo repo = new AppDataRepo(Pantry.this);
                        repo.clearUserIngredientsPantry();
                        repo.deleteIngredientsPantry();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        recyclerView.setAdapter(new PantryRecycleViewAdapter(new ArrayList<Ingredient>()));
                    }
                }.execute();
            }
        });
    }

    /**
     * This is method is responsible for getting a list of all ingredients that are to be selectable in the Pantry
     * autocomplete textbox. It calls an Async task which will run the getAllTolerant ingredients from the appdata repo -
     * ingredients that are in the database that the user hasn't noted an intolerance to, and return these to a new instance of the
     * Pantry AutoComplete adapter.
     */
    @SuppressLint("StaticFieldLeak")
    public void getPantryIngredient() {
        final AppDataRepo repository = new AppDataRepo(this);

        new AsyncTask<Void, Void, List<Ingredient>>() {
            @Override
            protected List<Ingredient> doInBackground(Void... voids) {
                List<Ingredient> categories = repository.getAllTolerantIngredients();
                categories.addAll(repository.getAllCategories());
                return categories;
            }

            @Override
            protected void onPostExecute(List<Ingredient> categories) {
                super.onPostExecute(categories);
                PantryAutocompleteAdapter adapter = new PantryAutocompleteAdapter(Pantry.this, categories);
                autoCompleteTextView.setAdapter(adapter);
            }
        }.execute();
    }


    /**
     * This method handles the adding of ingredients that were added into the pantry, into the pantry database.
     * This is performed as an Async task, which will call the AppRepo method to add the ingredient to the pantry.
     * <p>
     * It is possible for the end user to attempt to add an ingredient to the pantry twice - this causes an
     * SQLLiteConstraint exception which has been caught. A toast will also be shown to the user advising them that the ingredient already exists in the pantry.
     * <p>
     * After this background task is completed, the recyclerView will be updated with the new list of ingredients that exist in the pantry.
     */
    @SuppressLint("StaticFieldLeak")
    public void addPantryIngredientToDB(final Ingredient ingredient) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                AppDataRepo repo = new AppDataRepo(Pantry.this);
                try {
                    repo.addIngredientToPantry(ingredient);
                } catch (SQLiteConstraintException e) {
                    repo.insertLogs("Attempted to add an ingredient in the pantry that already existed");
                    return "Ingredient already exists in pantry";
                } finally {
                    repo.savePantryToUserDB();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String message) {
                super.onPostExecute(message);
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(Pantry.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    ((PantryRecycleViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).updateCategories(ingredient);
                }
            }
        }.execute();
    }

    /**
     * This method will load the contents of the pantry database. It is called when the user clicks the "My Pantry" button.
     * It will:
     * 1. Perform a query of the pantry database via an async task referenced in the AppDataRepo to get all records in the database, and return these values
     * 2. Pass that data to the pantry scene, and update the recyclerview with the items in the database
     */
    @SuppressLint("StaticFieldLeak")
    public void loadPantry() {
        new AsyncTask<Void, Void, List<Ingredient>>() {

            @Override
            protected List<Ingredient> doInBackground(Void... voids) {
                AppDataRepo repo = new AppDataRepo(Pantry.this);
                return repo.getAllPantryIngredients();
            }

            @Override
            protected void onPostExecute(List<Ingredient> ingredients) {
                super.onPostExecute(ingredients);
                ((PantryRecycleViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).updateCategories(ingredients);
            }
        }.execute();
    }
}