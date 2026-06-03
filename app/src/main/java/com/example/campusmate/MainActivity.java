package com.example.campusmate;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Intent;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    android.widget.TextView txtPageTitle, txtContentTitle, txtDescription;
    android.widget.Button btnOpenAddEvent, btnFilterAll, btnFilterTech, btnFilterArt, btnFilterSport, btnFilterSocial;

    RecyclerView recyclerEvents;
    BottomNavigationView bottomNavigation;

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
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterTech = findViewById(R.id.btnFilterTech);
        btnFilterArt = findViewById(R.id.btnFilterArt);
        btnFilterSport = findViewById(R.id.btnFilterSport);
        btnFilterSocial = findViewById(R.id.btnFilterSocial);

        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnOpenAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        btnFilterAll.setOnClickListener(v -> loadApprovedEvents(null));
        btnFilterTech.setOnClickListener(v -> loadApprovedEvents("Teknoloji"));
        btnFilterArt.setOnClickListener(v -> loadApprovedEvents("Sanat"));
        btnFilterSport.setOnClickListener(v -> loadApprovedEvents("Spor"));
        btnFilterSocial.setOnClickListener(v -> loadApprovedEvents("Sosyal"));

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

        showFilterButtons(true);

        txtPageTitle.setText("CampusMate");
        txtContentTitle.setText("Onaylanan Etkinlikler");
        txtDescription.setText("");

        recyclerEvents.setVisibility(android.view.View.VISIBLE);

        loadApprovedEvents(null);
    }

    private void loadApprovedEvents(String categoryFilter) {
        eventList = new ArrayList<>();

        com.google.firebase.firestore.Query query = db.collection("events")
                .whereEqualTo("status", "approved");



        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                eventList.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    String date = document.getString("date");
                    String location = document.getString("location");
                    String description = document.getString("description");
                    String createdByName = document.getString("createdByName");
                    String category = document.getString("category");

                    if (category == null || category.isEmpty()) {
                        category = AIEventAnalyzer.detectCategory(title, description);
                    }

                    if (createdByName == null) {
                        createdByName = "Bilinmeyen Kullanıcı";
                    }
                    if (categoryFilter != null && !categoryFilter.equals(category)) {
                        continue;
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

                    if (categoryFilter == null) {
                        txtDescription.setText("Henüz onaylanmış etkinlik bulunmuyor.");
                    } else {
                        txtDescription.setText(categoryFilter + " kategorisinde onaylanmış etkinlik bulunmuyor.");
                    }

                } else {
                    txtDescription.setText("");
                    recyclerEvents.setVisibility(android.view.View.VISIBLE);
                    eventAdapter = new EventAdapter(eventList);
                    recyclerEvents.setAdapter(eventAdapter);
                }
            } else {
                recyclerEvents.setVisibility(android.view.View.GONE);
                txtDescription.setText("Etkinlikler yüklenirken hata oluştu.");
            }
        });
    }

    private void showEventsScreen() {
        btnOpenAddEvent.setVisibility(android.view.View.VISIBLE);

        showFilterButtons(false);

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
                            txtDescription.setText("");
                            recyclerEvents.setVisibility(android.view.View.VISIBLE);
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
        btnOpenAddEvent.setVisibility(android.view.View.GONE);

        showFilterButtons(false);

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
            txtDescription.setText("");
            eventAdapter = new EventAdapter(eventList, true);
            recyclerEvents.setAdapter(eventAdapter);
        }
    }

    private void showProfileScreen() {
        txtPageTitle.setText("Profil");
        txtContentTitle.setText("Kullanıcı Profili");

        btnOpenAddEvent.setVisibility(android.view.View.GONE);
        showFilterButtons(false);
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
                        if (email == null) email = auth.getCurrentUser().getEmail();
                        if (department == null) department = "Yazılım Mühendisliği";
                        if (classLevel == null) classLevel = "3. Sınıf";
                        if (role == null) role = "user";

                        String finalName = name;
                        String finalEmail = email;
                        String finalDepartment = department;
                        String finalClassLevel = classLevel;
                        String finalRole = role;

                        db.collection("events")
                                .whereEqualTo("createdBy", uid)
                                .get()
                                .addOnSuccessListener(eventSnapshots -> {

                                    int totalEvents = eventSnapshots.size();
                                    int approvedCount = 0;
                                    int pendingCount = 0;

                                    for (QueryDocumentSnapshot eventDoc : eventSnapshots) {
                                        String status = eventDoc.getString("status");

                                        if ("approved".equals(status)) {
                                            approvedCount++;
                                        } else {
                                            pendingCount++;
                                        }
                                    }

                                    SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
                                    int favoriteCount = prefs.getAll().size();

                                    String roleText = finalRole.equals("admin")
                                            ? "🛡 Admin"
                                            : "👥 Kullanıcı";

                                    txtDescription.setText(
                                            "━━━━━━━━━━━━━━━━\n" +
                                                    "👤 " + finalName + "\n" +
                                                    "📧 " + finalEmail + "\n" +
                                                    "🏫 " + finalDepartment + "\n" +
                                                    "🎓 " + finalClassLevel + "\n" +
                                                    "🔐 Rol: " + roleText + "\n" +
                                                    "━━━━━━━━━━━━━━━━\n\n" +

                                                    "📊 Kullanıcı İstatistikleri\n\n" +
                                                    "📅 Eklediği Etkinlik: " + totalEvents + "\n" +
                                                    "✅ Onaylanan Etkinlik: " + approvedCount + "\n" +
                                                    "⏳ Bekleyen Etkinlik: " + pendingCount + "\n" +
                                                    "⭐ Favori Etkinlik: " + favoriteCount + "\n\n"


                                    );
                                });

                    } else {
                        txtDescription.setText("Kullanıcı bilgisi bulunamadı.");
                    }
                })
                .addOnFailureListener(e -> {
                    txtDescription.setText("Profil bilgileri alınamadı.");
                });
    }

    private void showFilterButtons(boolean show) {
        int visibility = show ? android.view.View.VISIBLE : android.view.View.GONE;

        btnFilterAll.setVisibility(visibility);
        btnFilterTech.setVisibility(visibility);
        btnFilterArt.setVisibility(visibility);
        btnFilterSport.setVisibility(visibility);
        btnFilterSocial.setVisibility(visibility);
    }
}