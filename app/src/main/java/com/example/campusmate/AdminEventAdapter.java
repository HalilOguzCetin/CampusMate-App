package com.example.campusmate;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    List<Event> eventList;
    List<String> eventIdList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminEventAdapter(List<Event> eventList, List<String> eventIdList) {
        this.eventList = eventList;
        this.eventIdList = eventIdList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.txtTitle.setText(event.getTitle());

        holder.txtInfo.setText(
                "📅 " + event.getDate() +
                        "\n📍 " + event.getLocation() +
                        "\n👤 " + event.getDescription()
        );

        String title = event.getTitle();
        String description = event.getDescription();

        String category = AIEventAnalyzer.detectCategory(title, description);
        int score = AIEventAnalyzer.calculateScore(title, description);
        String result = AIEventAnalyzer.checkSuitability(score);
        String reason = AIEventAnalyzer.generateReason(title, description);

        holder.txtAiAnalysis.setText(
                "🤖 AI Analizi\n\n" +
                        "📂 Kategori: " + category +
                        "\n⭐ Skor: " + score +
                        "\n🧠 Sonuç: " + result +
                        "\n📝 Sebep: " + reason
        );

        if (result.equals("Uygun")) {
            holder.cardLayout.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else if (result.equals("Şüpheli")) {
            holder.cardLayout.setBackgroundColor(Color.parseColor("#FFF8E1"));
        } else {
            holder.cardLayout.setBackgroundColor(Color.parseColor("#FFEBEE"));
        }

        if ("approved".equals(event.getStatus())) {
            holder.txtStatus.setText("✅ Onaylandı");
            holder.btnApprove.setVisibility(View.GONE);
        } else {
            holder.txtStatus.setText("⏳ Onay Bekliyor");
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnApprove.setEnabled(true);
            holder.btnApprove.setText("Onayla");
        }

        String eventId = eventIdList.get(position);

        holder.btnApprove.setOnClickListener(v -> {
            db.collection("events")
                    .document(eventId)
                    .update("status", "approved")
                    .addOnSuccessListener(unused -> {
                        event.setStatus("approved");
                        holder.txtStatus.setText("✅ Onaylandı");
                        NotificationHelper.showNotification(
                                v.getContext(),
                                "CampusMate Admin",
                                "Etkinlik onaylandı."
                        );
                        holder.btnApprove.setVisibility(View.GONE);
                    });
        });

        holder.btnDelete.setOnClickListener(v -> {
            db.collection("events")
                    .document(eventId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        eventList.remove(position);
                        eventIdList.remove(position);
                        NotificationHelper.showNotification(
                                v.getContext(),
                                "CampusMate Admin",
                                "Etkinlik silindi."
                        );
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, eventList.size());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        LinearLayout cardLayout;
        TextView txtTitle, txtInfo, txtStatus, txtAiAnalysis;
        Button btnApprove, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            cardLayout = (LinearLayout) itemView;

            txtTitle = itemView.findViewById(R.id.txtEventTitle);
            txtInfo = itemView.findViewById(R.id.txtEventInfo);
            txtStatus = itemView.findViewById(R.id.txtEventStatus);
            txtAiAnalysis = itemView.findViewById(R.id.txtAiAnalysis);

            btnApprove = itemView.findViewById(R.id.btnApproveEvent);
            btnDelete = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }
}