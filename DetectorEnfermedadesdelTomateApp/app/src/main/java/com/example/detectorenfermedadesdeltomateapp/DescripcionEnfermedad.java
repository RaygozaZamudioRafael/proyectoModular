package com.example.detectorenfermedadesdeltomateapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class DescripcionEnfermedad extends AppCompatActivity {

    TextView nombreEnfermedad, descripcion, tratamiento;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descripcion_enfermedad);

        updateFB();
        //updateInfo();

    }

    private void updateFB() {

        setContentView(R.layout.activity_descripcion_enfermedad);
        nombreEnfermedad = findViewById(R.id.De_Enfermedad);
        Intent intent = getIntent();
        String intentValue = intent.getStringExtra("KEY_SENDER");
        nombreEnfermedad.setText(intentValue);
        descripcion = findViewById(R.id.txtDescripcion);
        tratamiento = findViewById(R.id.txtTratamiento);

        db.collection("enfermedades")
                .whereEqualTo("nombre", intentValue)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //Toast.makeText(getApplicationContext(), "OnComplete", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_SHORT).show();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                descripcion.setText(document.getString("descripcion"));
                                tratamiento.setText(document.getString("tratamiento"));
                                //usuario.username = document.getString("userName");

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

//    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
//    public void updateInfo(){
//        setContentView(R.layout.activity_descripcion_enfermedad);
//        textView = findViewById(R.id.De_Enfermedad);
//        Intent intent = getIntent();
//        String intentValue = intent.getStringExtra("KEY_SENDER");
//        textView.setText(intentValue);
//        descripcion = findViewById(R.id.txtDescripcion);
//        tratamiento = findViewById(R.id.txtTratamiento);
//        String url = "http://192.168.100.12/Fetch/fetchEnfermedad.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
//            response -> tratamiento.setText(response),
//            error -> Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
//
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<>();
//                params.put("enfermedadNombre","Moho de hoja");
//                params.put("tipoPeticion","tratamientoSugerido");
//                return params;
//            }
//        };
//
//        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,url,
//                response -> descripcion.setText(response),
//                error -> Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
//
//        ){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<>();
//                params.put("enfermedadNombre","Moho de hoja");
//                params.put("tipoPeticion","descripcionEnfermedad");
//                return params;
//            }
//        };
//
//        requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//        requestQueue.add(stringRequest2);
//
//    }

}