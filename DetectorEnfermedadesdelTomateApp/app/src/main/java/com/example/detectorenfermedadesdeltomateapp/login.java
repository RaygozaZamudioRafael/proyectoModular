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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class login extends AppCompatActivity {

    TextInputEditText textInputEditTextEmail, textInputEditTextPassword;
    Button buttonSingIn;
    TextView textViewSingUp;
    ProgressBar progressBar;

    UsuarioLocalAlmacenado loginULA;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEditTextEmail = findViewById(R.id.login_email);
        textInputEditTextPassword = findViewById(R.id.login_password);
        textViewSingUp = findViewById(R.id.signup_text);
        buttonSingIn = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_bar_login);

        loginULA = new UsuarioLocalAlmacenado(this);

        mAuth = FirebaseAuth.getInstance();

        textViewSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),signup.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password, email;

                email = String.valueOf(textInputEditTextEmail.getText());
                password = String.valueOf(textInputEditTextPassword.getText());

                if(!email.equals("") && !password.equals("")){

                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Inicio de sesison correcto", Toast.LENGTH_SHORT).show();
                                salvarUsuarioLocal();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    });

//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            String[] field = new String[2];
//                            field[0] = "username";
//                            field[1] = "password";
//
//                            String[] data = new String[2];
//                            data[0] = email;
//                            data[1] = password;
//                            PutData putData = new PutData("http://192.168.100.12/Login/login.php", "POST", field, data);
//                            if (putData.startPut()) {
//                                if (putData.onComplete()) {
//                                    progressBar.setVisibility(View.GONE);
//                                    String result = putData.getResult();
//                                    if(result.equals("Login Success")){
//                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//                                        salvarUsuarioLocal();
//                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                    else{
//                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            }
//
//                        }
//                    });


                }
                else {
                    Toast.makeText(getApplicationContext(), "All fields are required :'username','password'", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void salvarUsuarioLocal() {
        //Actualizar el metodo de login y la forma que toma datos de la base de datos para que regrese la informacion y poder almacenar
        //el correo y demas datos
        Usuario user = new Usuario(
                                   null,
                                   String.valueOf(textInputEditTextPassword.getText()),
                                   String.valueOf(textInputEditTextEmail.getText()));

        loginULA.storeUserData(user);
        loginULA.setLoggInUser(true);

    }
}