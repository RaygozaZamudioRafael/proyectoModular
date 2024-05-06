package com.example.detectorenfermedadesdeltomateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class RiesgosHuertos extends AppCompatActivity {
    Button cerrarVentana;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riesgos_huertos);
        cerrarVentana = findViewById(R.id.returnMainActivityRiesgosHuerto);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.riesgoshuertos1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.riesgoshuertos2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.riesgoshuertos3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.riesgoshuertos4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.riesgoshuertos5, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        cerrarVentana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}