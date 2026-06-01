package com.example.campusmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    EditText edtTitle, edtDate, edtLocation, edtDescription, edtLatitude, edtLongitude;
    Button btnSaveEvent;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        edtTitle = findViewById(R.id.edtTitle);
        edtDate = findViewById(R.id.edtDate);
        edtLocation = findViewById(R.id.edtLocation);
        edtDescription = findViewById(R.id.edtDescription);
        edtLatitude = findViewById(R.id.edtLatitude);
        edtLongitude = findViewById(R.id.edtLongitude);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = edtTitle.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String latText = edtLatitude.getText().toString().trim();
        String lngText = edtLongitude.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Lütfen zorunlu alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = 38.6120;
        double longitude = 27.4290;

        try {
            if (!latText.isEmpty()) latitude = Double.parseDouble(latText);
            if (!lngText.isEmpty()) longitude = Double.parseDouble(lngText);
        } catch (Exception e) {
            Toast.makeText(this, "Koordinat formatı hatalı", Toast.LENGTH_SHORT).show();
            return;
        }

        AIEventAnalyzer.Prediction prediction = AIEventAnalyzer.analyze(title, description);

        String category = prediction.category;
        int aiScore = prediction.score;
        String aiResult = prediction.result;
        String aiReason = prediction.reason;

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "guest";
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : "Bilinmiyor";

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("location", location);
        event.put("description", description);
        event.put("latitude", latitude);
        event.put("longitude", longitude);
        event.put("createdBy", userId);
        event.put("createdByEmail", userEmail);
        event.put("status", "pending");

        event.put("category", category);
        event.put("aiScore", aiScore);
        event.put("aiResult", aiResult);
        event.put("aiReason", aiReason);
        event.put("mlLibrary", "Yerel ML/NLP sınıflandırma algoritması");

        db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Etkinlik eklendi ve ML analizi yapıldı", Toast.LENGTH_SHORT).show();

                    NotificationHelper.showNotification(
                            AddEventActivity.this,
                            "CampusMate",
                            "Etkinliğin admin onayına gönderildi."
                    );

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Etkinlik eklenemedi: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}