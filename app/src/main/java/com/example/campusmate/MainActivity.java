package com.example.campusmate;

import android.os.Bundle;
import android.content.SharedPreferences;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    android.widget.TextView txtPageTitle, txtContentTitle, txtDescription;
    RecyclerView recyclerEvents;
    BottomNavigationView bottomNavigation;
    android.widget.Button btnOpenAddEvent;
    List<Event> eventList;
    EventAdapter eventAdapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        txtPageTitle = findViewById(R.id.txtPageTitle);
        txtContentTitle = findViewById(R.id.txtContentTitle);
        txtDescription = findViewById(R.id.txtDescription);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnOpenAddEvent = findViewById(R.id.btnOpenAddEvent);

        btnOpenAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        showHomeScreen();

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                showHomeScreen();
                return true;
            } else if (id == R.id.nav_events) {
                showEventsScreen();
                return true;
            } else if (id == R.id.nav_map) {
                showMapScreen();
                return true;
            } else if (id == R.id.nav_favorites) {
                showFavoritesScreen();
                return true;
            } else if (id == R.id.nav_profile) {
                showProfileScreen();
                return true;
            }

            return false;
        });
    }

    private void showHomeScreen() {
        btnOpenAddEvent.setVisibility(android.view.View.GONE);
        txtPageTitle.setText("CampusMate");
        txtContentTitle.setText("Onaylanan Etkinlikler");
        txtDescription.setText("");

        recyclerEvents.setVisibility(android.view.View.VISIBLE);

        eventList = new ArrayList<>();

        db.collection("events")
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String title = document.getString("title");
                            String date = document.getString("date");
                            String location = document.getString("location");
                            String description = document.getString("description");

                            String createdByName = document.getString("createdByName");

                            if (createdByName == null) {
                                createdByName = "Bilinmeyen Kullanıcı";
                            }

                            eventList.add(new Event(
                                    title,
                                    date,
                                    location,
                                    description,
                                    createdByName
                            ));
                        }

                        if (eventList.isEmpty()) {
                            recyclerEvents.setVisibility(android.view.View.GONE);
                            txtDescription.setText("Henüz onaylanmış etkinlik bulunmuyor.");
                        } else {
                            eventAdapter = new EventAdapter(eventList);
                            recyclerEvents.setAdapter(eventAdapter);
                        }
                    }
                });
    }

    private void showEventsScreen() {
        btnOpenAddEvent.setVisibility(android.view.View.VISIBLE);

        txtPageTitle.setText("Etkinliklerim");
        txtContentTitle.setText("Benim Eklediğim Etkinlikler");
        txtDescription.setText("");

        recyclerEvents.setVisibility(android.view.View.VISIBLE);

        if (auth.getCurrentUser() == null) {
            recyclerEvents.setVisibility(android.view.View.GONE);
            txtDescription.setText("Kullanıcı oturumu bulunamadı.");
            return;
        }

        String currentUserId = auth.getCurrentUser().getUid();

        eventList = new ArrayList<>();

        db.collection("events")
                .whereEqualTo("createdBy", currentUserId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String title = document.getString("title");
                            String date = document.getString("date");
                            String location = document.getString("location");
                            String description = document.getString("description");
                            String status = document.getString("status");

                            if (status == null) {
                                status = "pending";
                            }

                            String statusText = status.equals("approved")
                                    ? "Durum: Onaylandı"
                                    : "Durum: Beklemede";

                            String createdByName = document.getString("createdByName");

                            if (createdByName == null) {
                                createdByName = "Bilinmeyen Kullanıcı";
                            }

                            eventList.add(new Event(
                                    title,
                                    date,
                                    location + "\n" + statusText,
                                    description,
                                    createdByName
                            ));
                        }

                        if (eventList.isEmpty()) {
                            recyclerEvents.setVisibility(android.view.View.GONE);
                            txtDescription.setText("Henüz etkinlik eklemediniz.");
                        } else {
                            eventAdapter = new EventAdapter(eventList);
                            recyclerEvents.setAdapter(eventAdapter);
                        }
                    }
                });
    }
    private void showMapScreen() {

        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void showFavoritesScreen() {
        txtPageTitle.setText("Favoriler");
        txtContentTitle.setText("Favori Etkinliklerim");
        txtDescription.setText("");

        recyclerEvents.setVisibility(android.view.View.VISIBLE);

        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Map<String, ?> allFavorites = prefs.getAll();

        eventList = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allFavorites.entrySet()) {
            String title = entry.getKey();

            eventList.add(new Event(
                    title,
                    "Favori etkinlik",
                    "Yerel cihazda kayıtlı",
                    "Bu etkinlik SharedPreferences ile favorilere kaydedildi.",
                    "CampusMate"
            ));
        }

        if (eventList.isEmpty()) {
            recyclerEvents.setVisibility(android.view.View.GONE);
            txtDescription.setText("Henüz favori etkinlik eklenmedi.");
        } else {
            eventAdapter = new EventAdapter(eventList, true);
            recyclerEvents.setAdapter(eventAdapter);
        }
    }

    private void showProfileScreen() {
        txtPageTitle.setText("Profil");
        txtContentTitle.setText("Kullanıcı Profili");

        btnOpenAddEvent.setVisibility(android.view.View.GONE);
        recyclerEvents.setVisibility(android.view.View.GONE);

        if (auth.getCurrentUser() == null) {
            txtDescription.setText("Kullanıcı oturumu bulunamadı.");
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String department = documentSnapshot.getString("department");
                        String classLevel = documentSnapshot.getString("classLevel");
                        String role = documentSnapshot.getString("role");

                        if (name == null) name = "Belirtilmedi";
                        if (email == null) email = "Belirtilmedi";
                        if (department == null) department = "Belirtilmedi";
                        if (classLevel == null) classLevel = "Belirtilmedi";
                        if (role == null) role = "user";

                        txtDescription.setText(
                                "👤 Ad Soyad: " + name +
                                        "\n📧 E-posta: " + email +
                                        "\n🏫 Bölüm: " + department +
                                        "\n🎓 Sınıf: " + classLevel +
                                        "\n🔐 Rol: " + role
                        );

                    } else {
                        txtDescription.setText("Kullanıcı bilgisi bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    txtDescription.setText("Profil bilgileri alınamadı.");
                });
    }
}