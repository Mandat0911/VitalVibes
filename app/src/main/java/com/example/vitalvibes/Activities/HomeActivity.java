package com.example.vitalvibes.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.databinding.ActivityLoginBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String userRole;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donors");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user role from the database
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Donor donor = dataSnapshot.getValue(Donor.class);
                    if (donor != null) {
                        userRole = donor.getRole();  // Store the role
                        displayRoleBasedUI(userRole);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRoleBasedUI(String role) {
        if ("Admin".equals(role)) {
            // Display Admin features
            showAdminFeatures();
        } else {
            // Display Donor features
            showDonorFeatures();
        }
    }

    private void showAdminFeatures() {
        // Hide donor-specific UI elements and show admin-specific UI
        binding.AdminView.setVisibility(View.VISIBLE);
        binding.DonorView.setVisibility(View.GONE);
    }

    private void showDonorFeatures() {
        // Hide admin-specific UI elements and show donor-specific UI
        binding.AdminView.setVisibility(View.GONE);
        binding.DonorView.setVisibility(View.VISIBLE);
    }
}