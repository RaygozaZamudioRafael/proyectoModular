package com.example.detectorenfermedadesdeltomateapp;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.detectorenfermedadesdeltomateapp.R.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import java.util.Locale;
import android.os.CountDownTimer;
import android.provider.Settings;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    FloatingActionButton fab;

    Location locationActual = null;

    private LocationManager locationManager;


    private MyLocationListener mylistener;

    private String provider;

    private Criteria criteria;

    private DrawerLayout drawerLayout;

    Location location = null;
    int TAMANIO_IMAGEN = 224;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainActivityULA = new UsuarioLocalAlmacenado(this);

        camara = findViewById(id.ma_Foto);
        galeria = findViewById(id.ma_Galeria);
        descripcion = findViewById(id.ma_BTNmostrarDescripcion);
        registro = findViewById(id.ma_AnalisisServidor);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //prueba = findViewById(id.prueba);
        //prueba2 = findViewById(id.prueba2);

        resultado = findViewById(id.ma_resultado);
        //resultado2 = findViewById(id.ma_resultado2);
        imageView = findViewById(id.ma_Imagen);
        username = findViewById(id.textViewUsuario);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//---------------------------------------------------------------------------------------------------------------------------
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //default
        criteria.setCostAllowed(false);

        provider = LocationManager.GPS_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }



        mylistener = new MyLocationListener();

//---------------------------------------------------------------------------------------------------------------------------

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

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        */
    }

    private void callLocationListener() {




    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        }

    }

    private boolean autentificacion() {
        return mainActivityULA.getAuthLogInUser();
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mapaEpidemiologico) {
            Toast.makeText(this, "Abriendo mapa epidemiologico", Toast.LENGTH_LONG).show();
            Intent Intent = new Intent(getApplicationContext(), MapaEpidemiologico.class);
            startActivity(Intent);
        }
        else if (id == R.id.reportarEnfermedad) {
            Toast.makeText(this, "Enviando reporte", Toast.LENGTH_LONG).show();
           // resultado.setText("calculando coordenadas");
            subirReporteEnfermedad();
        }
        else if(id == R.id.nav_logout){
            Toast.makeText(this, "Saliendo de la cuenta", Toast.LENGTH_LONG).show();
            mainActivityULA.clearUserData();
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.nav_info_huerto_urbano) {
            Toast.makeText(this, "Info Huertos urbanos", Toast.LENGTH_LONG).show();
            Intent Intent = new Intent(getApplicationContext(), InfoHuertos.class);
            startActivity(Intent);
        }
        else if(id == R.id.nav_riesgos_huerto_urbano) {
            Toast.makeText(this, "Riesgos de huertos urbanos", Toast.LENGTH_LONG).show();
            Intent Intent = new Intent(getApplicationContext(), RiesgosHuertos.class);
            startActivity(Intent);
        }
        else if(id == R.id.nav_guia_huerto_urbano) {
            Toast.makeText(this, "Guia huertos urbanos", Toast.LENGTH_LONG).show();
            Intent Intent = new Intent(getApplicationContext(), TipsHuerto.class);
            startActivity(Intent);
        }
        else if(id == R.id.nav_app_tutorial) {
            Toast.makeText(this, "Abrir tutorial", Toast.LENGTH_LONG).show();

        }
        else{

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    private void subirReporteEnfermedad() {

        Toast.makeText(getApplicationContext(), "Registro iniciado", Toast.LENGTH_SHORT).show();

        Map<String, Object> map = new HashMap<>();
        map.put("idUsuario", idUsuario);
        map.put("idEnfermedad", resultado.getText());
        map.put("fechaHora", FieldValue.serverTimestamp());

        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Obteniendo ubicación ...");
        alertDialog.setMessage("00:08");
        alertDialog.show();

        new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {

                location = locationManager.getLastKnownLocation(provider);

                GeoPoint latLong;
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                //resultado.setText(String.valueOf(lat)+","+String.valueOf(lon));
                latLong = new GeoPoint(latitude,longitude);
                map.put("latLon", latLong);


                locationManager.removeUpdates(mylistener);

                db.collection("registroEnfermedades").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Enfermedad Registrada",Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(getApplicationContext(),login.class);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al guardar",Toast.LENGTH_SHORT).show();
                    }
                });


                alertDialog.dismiss();
            }
        }.start();



    }

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
            //confidences0[i] = confidences0[i] + confidences1[i] + confidences2[i];
            confidences0[i] = confidences2[i];
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
private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MainActivity.this, "" + location.getLatitude() + location.getLongitude(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(MainActivity.this, provider + "'s status changed to " + status + "!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(MainActivity.this, "Provider " + provider + " enabled!",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Provider " + provider + " disabled!",
                Toast.LENGTH_SHORT).show();
    }
}

}

