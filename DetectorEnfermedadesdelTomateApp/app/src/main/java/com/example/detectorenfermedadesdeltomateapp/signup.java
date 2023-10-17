package com.example.detectorenfermedadesdeltomateapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class signup extends AppCompatActivity {

    TextInputEditText textInputEditTextUsername, textInputEditTextPassword, textInputEditTextEmail;
    Button buttonSingUp;
    TextView textViewSingIn;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textInputEditTextUsername = findViewById(R.id.et_user_name);
        textInputEditTextEmail    = findViewById(R.id.et_email);
        textInputEditTextPassword = findViewById(R.id.et_password);

        buttonSingUp = findViewById(R.id.button_signup);

        textViewSingIn = findViewById(R.id.signin_text);

        progressBar = findViewById(R.id.progress_bar_signUp);

        textViewSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username, password, email;

                username = String.valueOf(textInputEditTextUsername.getText());
                password = String.valueOf(textInputEditTextPassword.getText());
                email    = String.valueOf(textInputEditTextEmail.getText());

                if(!username.equals("") && !password.equals("") && !email.equals("")){

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[3];
                            field[0] = "email";
                            field[1] = "username";
                            field[2] = "password";

                            String[] data = new String[3];
                            data[0] = email;
                            data[1] = username;
                            data[2] = password;
                            PutData putData = new PutData("http://192.168.100.9/Login/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "All fields are required :'username','password'", Toast.LENGTH_SHORT).show();
                }

            }
        });






    }
}