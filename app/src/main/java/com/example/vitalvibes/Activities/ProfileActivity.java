package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.databinding.ActivityProfileBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ChipNavigationBar chipNavigationBar;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ChipNavigationBar
        chipNavigationBar = findViewById(R.id.chipNavigationBarProfile);
        setUpChipNavigationBar();

        auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Donors").child(userId);

        fetchProfileData();
        binding.LogoutBtn.setOnClickListener(v -> logout());
        binding.EditBtn.setOnClickListener(v -> editProfile());
    }

    private void editProfile() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class); // Navigate to LoginActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
    }

    private void fetchProfileData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Donor donor = snapshot.getValue(Donor.class);
                    if (donor != null) {
                        // Bind data to UI using binding
                        binding.profileName.setText(donor.getName());
                        binding.profileEmail.setText(donor.getEmail());
                        binding.profilePhone.setText(donor.getPhoneNumber());
                        binding.profileDob.setText(donor.getDob());
                        binding.profileBloodType.setText(donor.getBloodType());
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(ProfileActivity.this, targetActivity));
    }

    private void setUpChipNavigationBar() {
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);
        chipNavigationBar.setItemSelected(R.id.profile, true); // Set default selected item

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Class<?> targetActivity = null;

            // Determine target activity based on the selected menu item
            if (id == R.id.home) {
                targetActivity = HomeActivity.class;
            } else if (id == R.id.createSite) {
                targetActivity = CreateSiteActivity.class;
            } else if (id == R.id.location) {
                targetActivity = LocationActivity.class;
            } else if (id == R.id.profile) {
                targetActivity = ProfileActivity.class;
            }

            // Only navigate if the selected activity is different from the current one
            if (targetActivity != null && !targetActivity.equals(this.getClass())) {
                navigateToActivity(targetActivity);
            }
        });
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    }


}
