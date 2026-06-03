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

public class AdminUsersActivity extends AppCompatActivity {

    RecyclerView recyclerAdminUsers;
    TextView txtEmptyUsers;

    List<String> userIdList;
    List<String> nameList;
    List<String> emailList;
    List<String> roleList;

    AdminUserAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        recyclerAdminUsers = findViewById(R.id.recyclerAdminUsers);
        txtEmptyUsers = findViewById(R.id.txtEmptyUsers);

        recyclerAdminUsers.setLayoutManager(new LinearLayoutManager(this));

        userIdList = new ArrayList<>();
        nameList = new ArrayList<>();
        emailList = new ArrayList<>();
        roleList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loadUsers();
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userIdList.clear();
                    nameList.clear();
                    emailList.clear();
                    roleList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String userId = document.getId();
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String role = document.getString("role");

                        if (name == null) name = "İsimsiz Kullanıcı";
                        if (email == null) email = "Email yok";
                        if (role == null) role = "user";

                        userIdList.add(userId);
                        nameList.add(name);
                        emailList.add(email);
                        roleList.add(role);
                    }

                    if (userIdList.isEmpty()) {
                        txtEmptyUsers.setVisibility(android.view.View.VISIBLE);
                        recyclerAdminUsers.setVisibility(android.view.View.GONE);
                    } else {
                        txtEmptyUsers.setVisibility(android.view.View.GONE);
                        recyclerAdminUsers.setVisibility(android.view.View.VISIBLE);
                    }

                    adapter = new AdminUserAdapter(userIdList, nameList, emailList, roleList);
                    recyclerAdminUsers.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Kullanıcılar alınamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}