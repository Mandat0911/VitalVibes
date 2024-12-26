package com.example.vitalvibes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.vitalvibes.Adapter.PicListAdapter;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHospitalDetailBinding;
import com.example.vitalvibes.model.Hospital;
import com.example.vitalvibes.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HospitalDetail extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ActivityHospitalDetailBinding binding;
    private Hospital object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital");

        // Inside HospitalDetail activity's onCreate method
        binding.ShowMapDetailBtn.setOnClickListener(v -> {
            String address = object.getAddress(); // Get address from the Hospital object
            if (address != null && !address.isEmpty()) {
                Intent intent = new Intent(HospitalDetail.this, LocationActivity.class);
                intent.putExtra("address", address); // Pass the address to LocationActivity
                startActivity(intent);
            } else {
                Toast.makeText(HospitalDetail.this, "Address is not available", Toast.LENGTH_SHORT).show();
            }
        });

        binding.EditSiteBtn.setOnClickListener(v -> {
            String hospitalId = object.getHospitalId(); // Get address from the Hospital object
            if (hospitalId != null && !hospitalId.isEmpty()) {
                Intent intent = new Intent(HospitalDetail.this, EditSiteActivity.class);
                intent.putExtra("hospitalId", hospitalId); // Pass the address to LocationActivity
                startActivity(intent);
            } else {
                Toast.makeText(HospitalDetail.this, "Address is not available", Toast.LENGTH_SHORT).show();
            }
        });

        getIntentExtra();
        setVariable();
        initList();
    }

    private void setVariable(){
        binding.TitleDetail.setText(object.getSiteName());
        binding.phoneDetail.setText(object.getMobile());
        binding.descriptionDetail.setText(object.getHospitalBio());
        binding.AddressDetail.setText(object.getAddress());
        binding.backBtnDetail.setOnClickListener(v -> finish());

        binding.distanceDetail.setText(object.getAddress());

        // Check if the current user is the owner
        String ownerUID = object.getOwnerUID(); // Get ownerUID from the hospital object
        String currentUserUID = mAuth.getCurrentUser().getUid(); // Get current user UID


        if (currentUserUID.equals(ownerUID)) {
            // Show the delete button only if the current user is the owner
            binding.DeleteSiteBtn.setVisibility(View.VISIBLE);
            binding.EditSiteBtn.setVisibility(View.VISIBLE);
            binding.DonorSiteBtn.setVisibility(View.GONE);

            // Handle delete button click
            binding.DeleteSiteBtn.setOnClickListener(v -> deleteSite());
        } else {
            // Hide the delete button if the current user is not the owner
            binding.DeleteSiteBtn.setVisibility(View.GONE);
            binding.EditSiteBtn.setVisibility(View.GONE);
            binding.DonorSiteBtn.setVisibility(View.VISIBLE);
            checkFollowStatus(currentUserUID);
            binding.DonorSiteBtn.setOnClickListener(v -> toggleFollow(currentUserUID));
        
        }
    }

    // Toggle follow/unfollow status
    @SuppressLint("UseCompatLoadingForDrawables")
    private void toggleFollow(String currentUserUID) {
        DatabaseReference donorFollowsRef = FirebaseDatabase.getInstance().getReference("DonorFollows");
        donorFollowsRef.child(currentUserUID).child(object.getHospitalId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Unfollow
                        donorFollowsRef.child(currentUserUID).child(object.getHospitalId()).removeValue()
                                .addOnCompleteListener(unfollowTask -> {
                                    if (unfollowTask.isSuccessful()) {
                                        binding.DonorSiteBtn.setText("Follow");
                                        binding.DonorSiteBtn.setBackground(getResources().getDrawable(R.drawable.green_bg_btn));
                                        Toast.makeText(HospitalDetail.this, "Unfollowed successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HospitalDetail.this, "Unfollow failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Follow
                        donorFollowsRef.child(currentUserUID).child(object.getHospitalId()).setValue(true)
                                .addOnCompleteListener(followTask -> {
                                    if (followTask.isSuccessful()) {
                                        binding.DonorSiteBtn.setText("Unfollow");
                                        binding.DonorSiteBtn.setBackground(getResources().getDrawable(R.drawable.delete_blue_bg_btn));
                                        Toast.makeText(HospitalDetail.this, "Followed successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HospitalDetail.this, "Follow failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    // Check if the user is already following
    private void checkFollowStatus(String currentUserUID) {
        DatabaseReference donorFollowsRef = FirebaseDatabase.getInstance().getReference("DonorFollows");
        donorFollowsRef.child(currentUserUID).child(object.getHospitalId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        binding.DonorSiteBtn.setText("Unfollow");
                        binding.DonorSiteBtn.setBackground(getResources().getDrawable(R.drawable.delete_blue_bg_btn));
                    } else {
                        binding.DonorSiteBtn.setText("Follow");
                        binding.DonorSiteBtn.setBackground(getResources().getDrawable(R.drawable.green_bg_btn));
                    }
                });
    }

    // Method to delete the site
    private void deleteSite() {
        String hospitalId = object.getHospitalId(); // Get the hospital ID from the object
        DatabaseReference donorFollowsRef = FirebaseDatabase.getInstance().getReference("DonorFollows");
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notifications");

        databaseReference.child(hospitalId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify donors who follow the site
                        donorFollowsRef.orderByChild(hospitalId).equalTo(true)
                                .get()
                                .addOnCompleteListener(donorTask -> {
                                    if (donorTask.isSuccessful() && donorTask.getResult().exists()) {
                                        donorTask.getResult().getChildren().forEach(donor -> {
                                            String donorId = donor.getKey();
                                            String notificationId = notificationsRef.push().getKey();
                                            Notification notification = new Notification(
                                                    notificationId,
                                                    "Site Deleted",
                                                    "The site " + object.getSiteName() + " has been deleted.",
                                                    System.currentTimeMillis(),
                                                    false
                                            );
                                            // Save the notification to the database
                                            notificationsRef.child(donorId).child(notificationId).setValue(notification)
                                                    .addOnCompleteListener(notificationTask -> {
                                                        if (notificationTask.isSuccessful()) {
                                                            Log.d("Notification", "Notification sent to donor: " + donorId);
                                                        } else {
                                                            Log.e("NotificationError", "Failed to notify donor: " + donorId);
                                                        }
                                                    });
                                        });
                                    } else {
                                        Log.d("DonorNotification", "No donors found following this site.");
                                    }
                                });

                        Toast.makeText(HospitalDetail.this, "Site deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    } else {
                        Toast.makeText(HospitalDetail.this, "Failed to delete site", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initList() {
        ArrayList<String> picList = new ArrayList<>(object.getPic());

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.mainPic);

        binding.picListDetail.setAdapter(new PicListAdapter(picList, binding.mainPic));
        binding.picListDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
    }

    private void getIntentExtra(){
        object = (Hospital) getIntent().getSerializableExtra("object");
    }
}