package com.example.detectorenfermedadesdeltomateapp;

import static com.example.detectorenfermedadesdeltomateapp.R.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadBinarioV10006;
import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadV10007;
import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadV320006;
import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadV60007;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int MY_PERMISSIONS_REQUEST;

    FusedLocationProviderClient fusedLocationClient;

    //LocationRequest locationRequest;

    //Declaracion variables
    Button camara, galeria, descripcion, registro;
    ImageView imageView;
    TextView resultado, resultado2, username, prueba, prueba2;

    String idUsuario = "";
    String pruebaTexto;

    UsuarioLocalAlmacenado mainActivityULA;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    int TAMANIO_IMAGEN = 224;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        mainActivityULA = new UsuarioLocalAlmacenado(this);

        camara = findViewById(id.ma_Foto);
        galeria = findViewById(id.ma_Galeria);
        descripcion = findViewById(id.ma_BTNmostrarDescripcion);
        registro = findViewById(id.ma_AnalisisServidor);
        //prueba = findViewById(id.prueba);
        //prueba2 = findViewById(id.prueba2);

        resultado = findViewById(id.ma_resultado);
        //resultado2 = findViewById(id.ma_resultado2);
        imageView = findViewById(id.ma_Imagen);
        username = findViewById(id.textViewUsuario);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);

            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivityULA.clearUserData();

            }
        });


        /*registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registroIntent = new Intent(getApplicationContext(), login.class);
                startActivity(registroIntent);

            }
        });
*/
        descripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultado.getText().equals(".")) {
                    Toast.makeText(getApplicationContext(), "Se requiere analizar una foto primero", Toast.LENGTH_SHORT).show();
                } else {
                    Intent senderIntent = new Intent(getApplicationContext(), DescripcionEnfermedad.class);
                    senderIntent.putExtra("KEY_SENDER", resultado.getText().toString());
                    startActivity(senderIntent);
                    //finish();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        obtenerPermisos();

        if (autentificacion() == true) {
            Usuario usuario = mainActivityULA.getLoggInUser();
            username.setText(usuario.email);

            db.collection("usuarios")
                    .whereEqualTo("email", usuario.email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idUsuario = document.getId();
                                    usuario.username = document.getString("userName");
                                    username.setText(usuario.username);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        } else {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }

    }

    private void obtenerPermisos() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }

    }

    private boolean autentificacion() {
        return mainActivityULA.getAuthLogInUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.objeto1) {
            Toast.makeText(this, "You clicked Opcion1", Toast.LENGTH_LONG).show();
        } else if (id == R.id.mapaEpidemiologico) {
            Toast.makeText(this, "You clicked mapaEpidemiologico", Toast.LENGTH_LONG).show();
            Intent senderIntent = new Intent(getApplicationContext(), MapaEpidemiologico.class);
            startActivity(senderIntent);
        } else if (id == R.id.reportarEnfermedad) {
            Toast.makeText(this, "Subir reporte", Toast.LENGTH_LONG).show();
            subirReporteEnfermedad();
        } else if (id == R.id.logOut) {
            Toast.makeText(this, "You clicked LogOut", Toast.LENGTH_LONG).show();
            mainActivityULA.clearUserData();
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {

        }
        return super.onOptionsItemSelected(item);
    }

    private void subirReporteEnfermedad() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("demo","onLocationResult: " + locationResult);
            }
        };

        Toast.makeText(getApplicationContext(), "Registro iniciado", Toast.LENGTH_SHORT).show();
        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", idUsuario);
        map.put("idEnfermedad", "test");

        map.put("fechaHora", FieldValue.serverTimestamp());

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            obtenerPermisos();
            return;
        }
        lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onLocationChanged(final Location location) {
                    }
                });

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //ArrayList<Double> latLong = new ArrayList<>();
        GeoPoint latLong;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        latLong = new GeoPoint(latitude,longitude);
        map.put("latLon", latLong);
        db.collection("registroEnfermedades").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Enfermedad Registrada",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),login.class);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al guardar",Toast.LENGTH_SHORT).show();
            }
        });


    }
    //AGREGAR BOTON LOGOUT DENTRO DEL MENU Y RECORDAR LIMPIAR EL ALMACENAMIENTO LOCARL DEL USUARIO.

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, TAMANIO_IMAGEN, TAMANIO_IMAGEN, true);
                clasificarImagenEnsamble(image);
            }else{
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),dat);
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, TAMANIO_IMAGEN, TAMANIO_IMAGEN, false);

                clasificarImagenEnsamble(image);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }



    public void clasificarImagenEnsamble(Bitmap image){

        float[] confidences0 = new float[6],confidences1 = new float[6],confidences2 = new float[6];
        int maxPos = 0;
        float maxConfidence = 0;

        try {
            ModeloDetectorEnfermedadV320006 model = ModeloDetectorEnfermedadV320006.newInstance(getApplicationContext());
            ModeloDetectorEnfermedadV60007 model3 = ModeloDetectorEnfermedadV60007.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*TAMANIO_IMAGEN*TAMANIO_IMAGEN*3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[TAMANIO_IMAGEN * TAMANIO_IMAGEN];
            image.getPixels(intValues,0, image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;

            for(int i = 0; i<TAMANIO_IMAGEN; i++){
                for(int j = 0; j< TAMANIO_IMAGEN; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModeloDetectorEnfermedadV320006.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //pruebaTexto = "Prueba: "+ "\n";


            confidences0 = outputFeature0.getFloatArray();
            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }

        try {
            ModeloDetectorEnfermedadV10007 model = ModeloDetectorEnfermedadV10007.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*TAMANIO_IMAGEN*TAMANIO_IMAGEN*3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[TAMANIO_IMAGEN * TAMANIO_IMAGEN];
            image.getPixels(intValues,0, image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;

            for(int i = 0; i<TAMANIO_IMAGEN; i++){
                for(int j = 0; j< TAMANIO_IMAGEN; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModeloDetectorEnfermedadV10007.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            confidences1 = outputFeature0.getFloatArray();

            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }

        try {
            ModeloDetectorEnfermedadV60007 model = ModeloDetectorEnfermedadV60007.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*TAMANIO_IMAGEN*TAMANIO_IMAGEN*3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[TAMANIO_IMAGEN * TAMANIO_IMAGEN];
            image.getPixels(intValues,0, image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;

            for(int i = 0; i<TAMANIO_IMAGEN; i++){
                for(int j = 0; j< TAMANIO_IMAGEN; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModeloDetectorEnfermedadV60007.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            confidences2 = outputFeature0.getFloatArray();

            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }

        //Sumar las confidencias para sacar la media

        for (int i = 0; i < confidences0.length; i++) {
            confidences0[i] = confidences0[i] + confidences1[i] + confidences2[i];
        }

        //sacar mayor confidencia y comparar el resultado
        for (int i = 0; i < confidences0.length; i++) {
            //pruebaTexto = pruebaTexto + confidences[i] + "\n";
            if (confidences0[i] > maxConfidence) {
                maxConfidence = confidences0[i];
                maxPos = i;
                //prueba.setText(pruebaTexto);
            }
            //prueba.setText(pruebaTexto);
        }
        String[] classes =  {
                "Acaros",
                "Moho de hoja",
                "Moho polvoriento",
                "Plaga",
                "Puntos de hoja",//Puntos de hoja
                "Virus del tomate"
        };
        //----------------------------------
        resultado.setText(classes[maxPos]);
        //------------------------------
    }
/*


    public void clasificarImagen(Bitmap image) {
        try {
            ModeloDetectorEnfermedadBinarioV10006 model = ModeloDetectorEnfermedadBinarioV10006.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, TAMANIO_IMAGEN, TAMANIO_IMAGEN, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * TAMANIO_IMAGEN * TAMANIO_IMAGEN * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            Log.d("shape", byteBuffer.toString());
            Log.d("shape", inputFeature0.getBuffer().toString());

            int[] intValues = new int[TAMANIO_IMAGEN * TAMANIO_IMAGEN];
            image.getPixels(intValues, 0, image.getWidth(),0,0,image.getWidth(), image.getHeight());

            int pixel = 0;

            for(int i = 0; i<TAMANIO_IMAGEN; i++){
                for(int j = 0; j< TAMANIO_IMAGEN; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModeloDetectorEnfermedadBinarioV10006.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            pruebaTexto = "Prueba: "+ "\n";
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                pruebaTexto = pruebaTexto + confidences[i] + "\n";
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
                prueba.setText(pruebaTexto);
            }

            String[] classes = {
                    "Enfermo",
                    "Saludable"
            };

            if(classes[maxPos] == "Saludable"){
                resultado.setText(classes[maxPos]);
                classifyImage2(image);
            }
            else{
                classifyImage2(image); //Esto es para una segunda clase cuando detecta la enfermedad
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void classifyImage3(Bitmap image){
        try {
            ModeloDetectorEnfermedadV10007 model = ModeloDetectorEnfermedadV10007.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*TAMANIO_IMAGEN*TAMANIO_IMAGEN*3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[TAMANIO_IMAGEN * TAMANIO_IMAGEN];
            image.getPixels(intValues,0, image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;

            for(int i = 0; i<TAMANIO_IMAGEN; i++){
                for(int j = 0; j< TAMANIO_IMAGEN; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModeloDetectorEnfermedadV10007.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            pruebaTexto = "Prueba2: "+ "\n";
            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            float a,b;

            for (int i = 0; i < confidences.length; i++) {
                pruebaTexto = pruebaTexto + confidences[i] + "\n";
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                    prueba2.setText(pruebaTexto);

                }
                prueba2.setText(pruebaTexto);
            }

            String[] classes =  {
                    "Acaros",
                    "Moho de hoja",
                    "Moho polvoriento",
                    "Plaga",
                    "Puntos de hoja",//Puntos de hoja
                    "Virus del tomate"
            };
            //----------------------------------
            resultado2.setText(classes[maxPos]);
            //------------------------------

            // Releases model resources if no longer used.
            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
*/
}