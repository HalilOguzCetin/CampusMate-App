package com.example.campusmate;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventDetailActivity extends AppCompatActivity {

    TextView txtTitle, txtDate, txtLocation, txtDescription;
    Button btnFavorite, btnNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Etkinlik Detayı");
        }

        txtTitle = findViewById(R.id.txtDetailTitle);
        txtDate = findViewById(R.id.txtDetailDate);
        txtLocation = findViewById(R.id.txtDetailLocation);
        txtDescription = findViewById(R.id.txtDetailDescription);
        btnFavorite = findViewById(R.id.btnDetailFavorite);
        btnNotify = findViewById(R.id.btnNotify);

        createNotificationChannel();

        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        String description = getIntent().getStringExtra("description");

        txtTitle.setText(title);
        txtDate.setText("📅 " + date);
        txtLocation.setText("📍 " + location);
        txtDescription.setText(description);

        btnFavorite.setOnClickListener(v -> {
            SharedPreferences prefs =
                    getSharedPreferences("favorites", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(title, title);
            editor.apply();

            Toast.makeText(this,
                    title + " favorilere eklendi ❤️",
                    Toast.LENGTH_SHORT).show();
        });

        btnNotify.setOnClickListener(v -> {
            showNotification(title);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "event_channel",
                    "Etkinlik Bildirimleri",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("CampusMate etkinlik hatırlatmaları");

            NotificationManager manager = getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String eventTitle) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        100
                );
                return;
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "event_channel")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("CampusMate Hatırlatma")
                        .setContentText(eventTitle + " etkinliğini unutma!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}