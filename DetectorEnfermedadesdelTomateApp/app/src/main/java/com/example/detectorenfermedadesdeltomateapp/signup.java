
/*
TODO: Forzar al usuario que la contraseña tenga por lo menos 6 caracteres ya que firebase solo
TODO: permite registrar el usuario asi.


*/
package com.example.detectorenfermedadesdeltomateapp;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    TextInputEditText textInputEditTextUsername, textInputEditTextPassword, textInputEditTextEmail;
    Button buttonSingUp;
    TextView textViewSingIn;
    ProgressBar progressBar;

    FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;



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

        mFireStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


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

                    registrarUsuario(username, password,email);
                /*
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
                            PutData putData = new PutData("http://192.168.100.12/Login/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if(result.equals("Sign Up Success")){
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),login.class);

                                        salvarUsuarioLocal(username, password,email);

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
                */
                }
                else {
                    Toast.makeText(getApplicationContext(), "All fields are required :'username','password'", Toast.LENGTH_SHORT).show();
                }

            }
        });






    }

    private void registrarUsuario(String username, String password, String email) {

    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            Toast.makeText(getApplicationContext(), "Registro iniciado",Toast.LENGTH_SHORT).show();
            String id = mAuth.getCurrentUser().getUid();
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("userName",username);
            map.put("password",password);
            map.put("email", email);

            mFireStore.collection("usuarios").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Usuario Registrado",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),login.class);

                    salvarUsuarioLocal(username, password,email);

                    startActivity(intent);
                    finish();
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error al guardar",Toast.LENGTH_SHORT).show();
                }
            });


        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getApplicationContext(), "Error al registrar",Toast.LENGTH_SHORT).show();
        }
    });
    }

    private void salvarUsuarioLocal(String Username, String Password, String Email) {



        Usuario usuario = new Usuario(Username, Password, Email);

    }
}