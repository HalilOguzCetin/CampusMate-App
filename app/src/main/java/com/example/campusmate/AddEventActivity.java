package com.example.campusmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    EditText edtTitle, edtDate, edtLocation, edtDescription;
    Button btnAddEvent, btnSelectLocation;

    FirebaseFirestore db;
    FirebaseAuth auth;

    double selectedLatitude = 38.49158;
    double selectedLongitude = 27.70638;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        edtTitle = findViewById(R.id.edtTitle);
        edtDate = findViewById(R.id.edtDate);
        edtLocation = findViewById(R.id.edtLocation);
        edtDescription = findViewById(R.id.edtDescription);

        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnSelectLocation = findViewById(R.id.btnSelectLocation);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddEventActivity.this, SelectLocationActivity.class);
            startActivityForResult(intent, 100);
        });

        btnAddEvent.setOnClickListener(v -> addEvent());
    }

    private void addEvent() {

        String title = edtTitle.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) ||
                TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(description)) {

            Toast.makeText(this,
                    "Tüm alanları doldurun",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this,
                    "Kullanıcı oturumu bulunamadı",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String userName = documentSnapshot.getString("name");

                    if (userName == null) {
                        userName = "Kullanıcı";
                    }

                    Map<String, Object> event = new HashMap<>();

                    event.put("title", title);
                    event.put("date", date);
                    event.put("location", location);
                    event.put("description", description);
                    event.put("latitude", selectedLatitude);
                    event.put("longitude", selectedLongitude);
                    event.put("createdBy", userId);
                    event.put("createdByEmail", userEmail);
                    event.put("createdByName", userName);
                    event.put("status", "pending");

                    db.collection("events")
                            .add(event)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this,
                                        "Etkinlik gönderildi. Admin onayı bekleniyor.",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Etkinlik eklenemedi: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Kullanıcı bilgisi alınamadı: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            selectedLatitude = data.getDoubleExtra("latitude", 38.49158);
            selectedLongitude = data.getDoubleExtra("longitude", 27.70638);

            Toast.makeText(this,
                    "Konum seçildi",
                    Toast.LENGTH_SHORT).show();
        }
    }
}