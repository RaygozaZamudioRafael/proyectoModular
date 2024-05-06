package com.example.detectorenfermedadesdeltomateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class InfoHuertos extends AppCompatActivity {
    Button cerrarVentana;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_huertos);
        cerrarVentana = findViewById(R.id.returnMainActivityInfoHuerto);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.infohuertourbano1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.infohuertourbano2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.infohuertourbano3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.infohuertourbano4, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        cerrarVentana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}