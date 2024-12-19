package com.example.vitalvibes.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vitalvibes.Adapter.HosptalListAdapter;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.model.Donor;
import com.example.vitalvibes.model.Hospital;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database

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

    private void initCategory() {
        DatabaseReference myCategory = firebaseDatabase.getReference("Popular");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Hospital> list = new ArrayList<>();

        // Set up RecyclerView LayoutManager
        binding.homeMainView.setLayoutManager(new GridLayoutManager(HomeActivity.this, 4));

        myCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Hospital hospital = issue.getValue(Hospital.class);
                        if (hospital != null) {
                            list.add(hospital);
                        }
                    }
                    if (!list.isEmpty()) {
                        RecyclerView.Adapter adapter = new HosptalListAdapter(list);
                        binding.homeMainView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                        binding.homeMainView.setAdapter(adapter);
                    } else {
                        Toast.makeText(HomeActivity.this, "No categories available.", Toast.LENGTH_SHORT).show();
                    }
                }
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
                Toast.makeText(HomeActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
                binding.progressBarCategory.setVisibility(View.GONE);
            }
        });
    }


    private void displayRoleBasedUI(String role) {
        if ("Admin".equals(role)) {
            // Display Admin features
            showAdminFeatures();
        } else {
            // Display Donor features
            initCategory();
        }
    }

    private void showAdminFeatures() {
        // Hide donor-specific UI elements and show admin-specific UI
        binding.HomeGreeting.setVisibility(View.GONE);
//        binding.DonorView.setVisibility(View.GONE);
    }
//
//    private void showDonorFeatures() {
//        // Hide admin-specific UI elements and show donor-specific UI
//        binding.AdminView.setVisibility(View.GONE);
//        binding.DonorView.setVisibility(View.VISIBLE);
//    }
}