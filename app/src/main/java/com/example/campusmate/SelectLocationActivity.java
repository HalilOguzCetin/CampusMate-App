package com.example.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.selectMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng faculty = new LatLng(38.49158, 27.70638);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(faculty, 16));

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;

            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Seçilen Konum"));

            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", latLng.latitude);
            resultIntent.putExtra("longitude", latLng.longitude);

            setResult(RESULT_OK, resultIntent);

            Toast.makeText(this, "Konum seçildi", Toast.LENGTH_SHORT).show();

            finish();
        });
    }
}