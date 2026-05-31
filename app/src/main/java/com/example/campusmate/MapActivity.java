package com.example.campusmate;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Kampüs Haritası");
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        LatLng campus = new LatLng(38.6826, 27.3139);

        mMap.addMarker(new MarkerOptions()
                .position(campus)
                .title("CBÜ Kampüs"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, 15));

        loadEventMarkers();
    }

    private void loadEventMarkers() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int markerCount = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String title = document.getString("title");
                        Double latitude = document.getDouble("latitude");
                        Double longitude = document.getDouble("longitude");

                        if (title != null && latitude != null && longitude != null) {

                            LatLng eventLocation = new LatLng(latitude, longitude);

                            mMap.addMarker(new MarkerOptions()
                                    .position(eventLocation)
                                    .title(title));

                            markerCount++;
                        }
                    }

                    Toast.makeText(this,
                            markerCount + " etkinlik haritada gösterildi",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Etkinlikler haritaya yüklenemedi",
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}