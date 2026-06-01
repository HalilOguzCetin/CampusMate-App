package com.example.campusmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    List<String> userIdList;
    List<String> nameList;
    List<String> emailList;
    List<String> roleList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminUserAdapter(List<String> userIdList, List<String> nameList, List<String> emailList, List<String> roleList) {
        this.userIdList = userIdList;
        this.nameList = nameList;
        this.emailList = emailList;
        this.roleList = roleList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userId = userIdList.get(position);
        String name = nameList.get(position);
        String email = emailList.get(position);
        String role = roleList.get(position);

        holder.txtUserName.setText("👤 " + name);
        holder.txtUserEmail.setText("✉️ " + email);
        holder.txtUserRole.setText("Rol: " + ("admin".equals(role) ? "🛡 Admin" : "👥 Kullanıcı"));
        if ("admin".equals(role)) {
            holder.btnMakeAdmin.setText("Kullanıcı Yap");
            holder.btnMakeAdmin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800"))
            );
        } else {
            holder.btnMakeAdmin.setText("Admin Yap");
            holder.btnMakeAdmin.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50"))
            );
        }

        holder.btnMakeAdmin.setOnClickListener(v -> {
            String newRole = "admin".equals(roleList.get(position)) ? "user" : "admin";

            db.collection("users")
                    .document(userId)
                    .update("role", newRole)
                    .addOnSuccessListener(unused -> {
                        roleList.set(position, newRole);
                        notifyItemChanged(position);

                        String message = newRole.equals("admin")
                                ? "Kullanıcı admin yapıldı"
                                : "Admin kullanıcıya çevrildi";

                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                        NotificationHelper.showNotification(
                                v.getContext(),
                                "CampusMate Admin",
                                newRole.equals("admin")
                                        ? "Kullanıcı admin rolüne yükseltildi."
                                        : "Admin rolü kullanıcıya çevrildi."
                        );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "İşlem başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        holder.btnDeleteUser.setOnClickListener(v -> {
            db.collection("users")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        userIdList.remove(position);
                        nameList.remove(position);
                        emailList.remove(position);
                        roleList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(v.getContext(), "Kullanıcı Firestore listesinden silindi", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(), "Silme başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return userIdList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName, txtUserEmail, txtUserRole;
        Button btnMakeAdmin, btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserEmail = itemView.findViewById(R.id.txtUserEmail);
            txtUserRole = itemView.findViewById(R.id.txtUserRole);
            btnMakeAdmin = itemView.findViewById(R.id.btnMakeAdmin);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}