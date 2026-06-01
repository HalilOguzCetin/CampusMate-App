package com.example.campusmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    List<Event> eventList;
    boolean isFavoritesScreen;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
        this.isFavoritesScreen = false;
    }

    public EventAdapter(List<Event> eventList, boolean isFavoritesScreen) {
        this.eventList = eventList;
        this.isFavoritesScreen = isFavoritesScreen;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = eventList.get(position);

        String createdByName = event.getCreatedByName();

        if (createdByName == null || createdByName.trim().isEmpty()) {
            createdByName = "CampusMate";
        }

        String firstLetter = createdByName.substring(0, 1).toUpperCase();

        holder.txtAvatar.setText(firstLetter);
        holder.txtCreatedByName.setText("Ekleyen: " + createdByName);

        holder.txtTitle.setText(event.getTitle());
        holder.txtDate.setText(event.getDate());
        holder.txtLocation.setText(event.getLocation());

        holder.itemView.setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(v.getContext(),
                            EventDetailActivity.class);

            intent.putExtra("title", event.getTitle());
            intent.putExtra("date", event.getDate());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("description", event.getDescription());

            v.getContext().startActivity(intent);
        });

        if (isFavoritesScreen) {
            holder.btnFavorite.setText("Favorilerden Çıkar");
        } else {
            holder.btnFavorite.setText("Favoriye Ekle");
        }

        holder.btnFavorite.setOnClickListener(v -> {

            Context context = v.getContext();

            SharedPreferences prefs =
                    context.getSharedPreferences("favorites", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();

            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            if (isFavoritesScreen) {

                editor.remove(event.getTitle());
                editor.apply();

                eventList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, eventList.size());

                Toast.makeText(context,
                        event.getTitle() + " favorilerden çıkarıldı",
                        Toast.LENGTH_SHORT).show();

            } else {

                editor.putString(event.getTitle(), event.getTitle());
                editor.apply();

                Toast.makeText(context,
                        event.getTitle() + " favorilere eklendi ❤️",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView txtAvatar, txtCreatedByName;
        TextView txtTitle, txtDate, txtLocation;
        Button btnFavorite;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAvatar = itemView.findViewById(R.id.txtAvatar);
            txtCreatedByName = itemView.findViewById(R.id.txtCreatedByName);

            txtTitle = itemView.findViewById(R.id.txtEventTitle);
            txtDate = itemView.findViewById(R.id.txtEventDate);
            txtLocation = itemView.findViewById(R.id.txtEventLocation);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}