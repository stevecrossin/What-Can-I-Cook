package com.stevecrossin.whatcanicook.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stevecrossin.whatcanicook.R;
import com.stevecrossin.whatcanicook.roomdatabase.AppDataRepo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class About extends AppCompatActivity {

    /*
    Loads About Activity when the class is called/created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Declaration of elements - being the textview and the source of text
        TextView aboutText = findViewById(R.id.aboutTextView);

        InputStream textInputStream = getResources().openRawResource(R.raw.about);

        ByteArrayOutputStream textOutputStream = new ByteArrayOutputStream();

        int t;
        try {
            t = textInputStream.read();
            while (t != -1) {
                textOutputStream.write(t);
                t = textInputStream.read();
            }
            textInputStream.close();
        } catch (IOException e) {
            new AppDataRepo(About.this).insertLogs("Error getting about text - file doesn't exist");
            Toast.makeText(About.this, "Ingredient already exists in pantry", Toast.LENGTH_SHORT).show();
        }
        aboutText.setText(textOutputStream.toString());
    }


    //Navigate to Log View
    public void viewLogs(View view) {
        Intent intent = new Intent(this, Logs.class);
        startActivity(intent);
    }

}
