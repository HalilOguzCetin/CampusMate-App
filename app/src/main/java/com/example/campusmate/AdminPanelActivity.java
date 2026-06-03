package com.example.campusmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPanelActivity extends AppCompatActivity {

    Button btnUsers, btnEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        btnUsers = findViewById(R.id.btnUsers);
        btnEvents = findViewById(R.id.btnEvents);

        btnUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this,
                    AdminUsersActivity.class);
            startActivity(intent);
        });

        btnEvents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this,
                    AdminEventsActivity.class);
            startActivity(intent);
        });
    }
}