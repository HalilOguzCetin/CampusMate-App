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
                .title("CBÜ Kampüs")
                .snippet("Manisa Celal Bayar Üniversitesi"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campus, 15));

        loadEventMarkers();
    }

    private void loadEventMarkers() {
        db.collection("events")
                .whereEqualTo("status", "approved")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int markerCount = 0;
                    LatLng firstEventLocation = null;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String title = document.getString("title");
                        String date = document.getString("date");
                        String category = document.getString("category");
                        String location = document.getString("location");
                        String description = document.getString("description");

                        if (title == null) title = "Başlıksız Etkinlik";

                        if (category == null || category.isEmpty()) {
                            category = AIEventAnalyzer.detectCategory(
                                    title,
                                    description != null ? description : ""
                            );
                        }

                        Double latitude = getDoubleValue(document, "latitude");
                        Double longitude = getDoubleValue(document, "longitude");

                        if (latitude == null || longitude == null) {
                            continue;
                        }

                        LatLng eventLocation = new LatLng(latitude, longitude);

                        if (firstEventLocation == null) {
                            firstEventLocation = eventLocation;
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(eventLocation)
                                .title(title)
                                .snippet(
                                        "Kategori: " + category +
                                                " | Tarih: " + (date != null ? date : "Tarih yok") +
                                                " | Konum: " + (location != null ? location : "Konum yok")
                                ));

                        markerCount++;
                    }

                    if (firstEventLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstEventLocation, 14));
                    }

                    Toast.makeText(this,
                            markerCount + " onaylı etkinlik haritada gösterildi",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Etkinlikler haritaya yüklenemedi: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });

    }

    private Double getDoubleValue(QueryDocumentSnapshot document, String fieldName) {
        Object value = document.get(fieldName);

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }

        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}