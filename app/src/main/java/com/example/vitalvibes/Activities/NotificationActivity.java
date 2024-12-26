package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.vitalvibes.Adapter.NotificationAdapter;
import com.example.vitalvibes.databinding.ActivityNotificationBinding;
import com.example.vitalvibes.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private ArrayList<Notification> notificationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        binding.backBtnNoti.setOnClickListener(v -> navigateToActivity(HomeActivity.class));
        fetchNotificationData();
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(this, notificationsList);
        binding.NotiRecyleView.setLayoutManager(new LinearLayoutManager(this));
        binding.NotiRecyleView.setAdapter(notificationAdapter);
    }

    private void fetchNotificationData() {
        binding.progressBarNoti.setVisibility(View.VISIBLE);

        DatabaseReference myNoti = FirebaseDatabase.getInstance().getReference("Notifications");
        myNoti.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Notification notification = issue.getValue(Notification.class);
                        if (notification != null) {
                            notificationsList.add(notification);
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(NotificationActivity.this, "No notifications available.", Toast.LENGTH_SHORT).show();
                }
                binding.progressBarNoti.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationActivity.this, "Error fetching notifications.", Toast.LENGTH_SHORT).show();
                binding.progressBarNoti.setVisibility(View.GONE);
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(NotificationActivity.this, targetActivity));
        finish();
    }
}
