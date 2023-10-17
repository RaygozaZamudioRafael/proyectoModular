package com.example.detectorenfermedadesdeltomateapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class DetectorEnfermedadesActivity extends AppCompatActivity {

    protected String mScreenName;

    protected SharedPreferences prefs;

    public abstract void setActivityName();
    public abstract void onVolleyResponse(String response);

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("DetectorEnfermedades", MODE_PRIVATE);
        setActivityName();
    }

    public void displayAlertDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }




}
