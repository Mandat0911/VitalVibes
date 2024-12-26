package com.example.vitalvibes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ViewholderNotificationBinding;
import com.example.vitalvibes.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Notification> notificationList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList != null ? notificationList : new ArrayList<>();
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderNotificationBinding binding = ViewholderNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.binding.NotiTitle.setText(notification.getTitle());
        holder.binding.detailNoti.setText(notification.getMessage());
        holder.binding.timeStampNoti.setText(formatTimeStamp(notification.getTimeStamp()));

        if (notification.isRead()) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.lightGreen));
        }

        holder.itemView.setOnClickListener(v -> markNotificationRead(notification, position));
    }

    private void markNotificationRead(Notification notification, int position) {
        String donorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("users")
                .child(donorId)
                .child("notifications")
                .child(notification.getId());

        notificationRef.child("isRead").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notification.setRead(true);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Failed to mark as read!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTimeStamp(long timeStamp) {
        return android.text.format.DateFormat.format("dd/MM/yyyy hh:mm a", timeStamp).toString();
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderNotificationBinding binding;

        public ViewHolder(ViewholderNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
