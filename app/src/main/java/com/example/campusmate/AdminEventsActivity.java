package com.example.campusmate;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminEventsActivity extends AppCompatActivity {

    ListView listAdminEvents;
    TextView txtEmptyEvents;
    ArrayList<String> eventList;
    ArrayList<String> eventIdList;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        listAdminEvents = findViewById(R.id.listAdminEvents);
        txtEmptyEvents = findViewById(R.id.txtEmptyEvents);

        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, R.layout.item_admin_list, R.id.txtAdminItem, eventList);
        listAdminEvents.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadEvents();

        listAdminEvents.setOnItemClickListener((parent, view, position, id) -> {
            String eventId = eventIdList.get(position);
            showEventOptions(eventId);
        });
    }

    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    eventIdList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventId = document.getId();
                        String title = document.getString("title");
                        String date = document.getString("date");
                        String location = document.getString("location");
                        String status = document.getString("status");
                        String createdByEmail = document.getString("createdByEmail");

                        if (title == null) title = "Başlıksız";
                        if (date == null) date = "Tarih yok";
                        if (location == null) location = "Konum yok";
                        if (status == null) status = "pending";
                        if (createdByEmail == null) createdByEmail = "Bilinmiyor";

                        eventIdList.add(eventId);

                        eventList.add(
                                "Başlık: " + title +
                                        "\nTarih: " + date +
                                        "\nKonum: " + location +
                                        "\nEkleyen: " + createdByEmail +
                                        "\nDurum: " + (status.equals("approved") ? "✅ Onaylandı" : "⏳ Beklemede")
                        );
                    }

                    if (eventList.isEmpty()) {
                        txtEmptyEvents.setVisibility(android.view.View.VISIBLE);
                        listAdminEvents.setVisibility(android.view.View.GONE);
                    } else {
                        txtEmptyEvents.setVisibility(android.view.View.GONE);
                        listAdminEvents.setVisibility(android.view.View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Etkinlikler alınamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void showEventOptions(String eventId) {
        String[] options = {"Onayla", "Sil"};

        new AlertDialog.Builder(this)
                .setTitle("Etkinlik İşlemi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        approveEvent(eventId);
                    } else if (which == 1) {
                        deleteEvent(eventId);
                    }
                })
                .show();
    }

    private void approveEvent(String eventId) {
        db.collection("events")
                .document(eventId)
                .update("status", "approved")
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Etkinlik onaylandı", Toast.LENGTH_SHORT).show();
                    loadEvents();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Onaylama hatası: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void deleteEvent(String eventId) {
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Etkinlik silindi", Toast.LENGTH_SHORT).show();
                    loadEvents();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Silme hatası: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}