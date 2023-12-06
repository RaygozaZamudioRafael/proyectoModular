package com.example.detectorenfermedadesdeltomateapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadBinarioV10006;
import com.example.detectorenfermedadesdeltomateapp.ml.ModeloDetectorEnfermedadV320006;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    //Declaracion variables
    Button camara, galeria, descripcion, registro;
    ImageView imageView;
    TextView resultado;



    int TAMANIO_IMAGEN = 224;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camara  = findViewById(R.id.ma_Foto);
        galeria = findViewById(R.id.ma_Galeria);
        descripcion = findViewById(R.id.ma_BTNmostrarDescripcion);
        registro = findViewById(R.id.ma_login);

        resultado   = findViewById(R.id.ma_resultado);
        imageView   = findViewById(R.id.ma_Imagen);

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,3);
                }else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(cameraIntent,1);

            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registroIntent = new Intent(getApplicationContext(), login.class);
                startActivity(registroIntent);

            }
        });

        descripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resultado.getText().equals("")){
                    Toast.makeText(getApplicationContext(), "Se requiere analizar una foto primero", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent senderIntent = new Intent(getApplicationContext(),DescripcionEnfermedad.class);
                    senderIntent.putExtra("KEY_SENDER", resultado.getText().toString());
                    startActivity(senderIntent);
                    //finish();
                }
            }
        });
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
                clasificarImagen(image);
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
                clasificarImagen(image);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

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

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {
                    "Enfermo",
                    "Saludable"
            };

            if(classes[maxPos] == "Saludable"){
                resultado.setText(classes[maxPos]);
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


    public void classifyImage2(Bitmap image){
        try {
            ModeloDetectorEnfermedadV320006 model = ModeloDetectorEnfermedadV320006.newInstance(getApplicationContext());

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

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes =  {
                    "Acaros",
                    "Moho de hoja",
                    "Moho polvoriento",
                    "Plaga",
                    "Moho de hoja",//Puntos de hoja
                    "Virus_del_tomate"
            };
            //----------------------------------
            resultado.setText(classes[maxPos]);
            //------------------------------

            // Releases model resources if no longer used.
            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

}