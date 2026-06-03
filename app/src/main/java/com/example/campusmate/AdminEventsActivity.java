package com.example.campusmate;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventsActivity extends AppCompatActivity {

    RecyclerView recyclerAdminEvents;
    TextView txtEmptyEvents;

    List<Event> eventList;
    List<String> eventIdList;

    AdminEventAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        recyclerAdminEvents = findViewById(R.id.recyclerAdminEvents);
        txtEmptyEvents = findViewById(R.id.txtEmptyEvents);

        recyclerAdminEvents.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loadEvents();
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
                        String createdByEmail = document.getString("createdByEmail");
                        String status = document.getString("status");
                        if (status == null) status = "pending";

                        if (title == null) title = "Başlıksız";
                        if (date == null) date = "Tarih yok";
                        if (location == null) location = "Konum yok";
                        if (createdByEmail == null) createdByEmail = "Ekleyen: Bilinmiyor";

                        eventList.add(new Event(title, date, location, createdByEmail, status));
                        eventIdList.add(eventId);
                    }

                    if (eventList.isEmpty()) {
                        txtEmptyEvents.setVisibility(android.view.View.VISIBLE);
                        recyclerAdminEvents.setVisibility(android.view.View.GONE);
                    } else {
                        txtEmptyEvents.setVisibility(android.view.View.GONE);
                        recyclerAdminEvents.setVisibility(android.view.View.VISIBLE);
                    }

                    adapter = new AdminEventAdapter(eventList, eventIdList);
                    recyclerAdminEvents.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Etkinlikler alınamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}