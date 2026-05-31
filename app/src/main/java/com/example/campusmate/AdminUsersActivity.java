package com.example.campusmate;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    ListView listUsers;
    ArrayList<String> userList;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        listUsers = findViewById(R.id.listUsers);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listUsers.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadUsers();
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String role = document.getString("role");

                        if (name == null) name = "İsimsiz";
                        if (email == null) email = "Email yok";
                        if (role == null) role = "user";

                        userList.add("Ad: " + name + "\nEmail: " + email + "\nRol: " + role);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Kullanıcılar alınamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}