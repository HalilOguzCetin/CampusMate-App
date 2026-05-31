package com.example.campusmate;

import android.os.Bundle;
import android.content.SharedPreferences;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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

    List<Event> eventList;
    EventAdapter eventAdapter;

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        txtPageTitle = findViewById(R.id.txtPageTitle);
        txtContentTitle = findViewById(R.id.txtContentTitle);
        txtDescription = findViewById(R.id.txtDescription);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        bottomNavigation = findViewById(R.id.bottomNavigation);


        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
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
        txtPageTitle.setText("CampusMate");
        txtContentTitle.setText("Yaklaşan Etkinlikler");
        txtDescription.setText("");

        recyclerEvents.setVisibility(android.view.View.VISIBLE);

        eventList = new ArrayList<>();
        eventList.add(new Event("Yapay Zeka Semineri", "20 Mayıs 2026 - 14:00", "Mühendislik Fakültesi", "Yapay zeka ve makine öğrenmesi semineri"));
        eventList.add(new Event("Bahar Şenliği", "25 Mayıs 2026 - 18:00", "Kampüs Açık Alanı", "Müzik ve öğrenci etkinlikleri"));
        eventList.add(new Event("Kariyer Günleri", "28 Mayıs 2026 - 10:00", "Konferans Salonu", "Şirketlerle kariyer buluşması"));

        eventAdapter = new EventAdapter(eventList);
        recyclerEvents.setAdapter(eventAdapter);
    }

    private void showEventsScreen() {

        txtPageTitle.setText("Etkinlikler");
        txtContentTitle.setText("Firebase Etkinlikleri");
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

                            eventList.add(new Event(title, date, location, description));
                        }

                        eventAdapter = new EventAdapter(eventList);
                        recyclerEvents.setAdapter(eventAdapter);
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
                    "Bu etkinlik SharedPreferences ile favorilere kaydedildi."
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

        recyclerEvents.setVisibility(android.view.View.GONE);
        txtDescription.setText("Ad Soyad: Öğrenci\nBölüm: Yazılım Mühendisliği\nUygulama: CampusMate");
    }
}