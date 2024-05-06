package com.example.detectorenfermedadesdeltomateapp;
//TODO: Agregar la verificacion de los permisos
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.detectorenfermedadesdeltomateapp.databinding.ActivityMapaEpidemiologicoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MapaEpidemiologico extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
private ActivityMapaEpidemiologicoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMapaEpidemiologicoBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng m1 = new LatLng(20.647427268670363, -103.38594934868995);
//        mMap.addMarker(new MarkerOptions().position(m1).title("Marker 1"));
        LatLng m2 = new LatLng(20.64717925515873, -103.38994699278027);
        mMap.addMarker(new MarkerOptions().position(m2).title("Marker 2"));

        marcadoresEnDB(mMap);

//        LatLng m3 = new LatLng(20.644079052131655, -103.38895310336554);
//        mMap.addMarker(new MarkerOptions().position(m3).title("Marker 3"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(m1));
        // WnsrR0wVvlf4kGxGt27kG06OAtx2  OUjZSIqwufDVyjeZC6ks
    }

    private void marcadoresEnDB(GoogleMap mMap) {


        db.collection("registroEnfermedades")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            LatLng marcador = new LatLng(0,0);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint point = document.getGeoPoint("latLon");
                                marcador = new LatLng(point.getLatitude(), point.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(marcador).title(document.getString("idEnfermedad")));

                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marcador,15));

                        } else {
                            Toast.makeText(getApplicationContext(), "Error al recuperar datos", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }
}