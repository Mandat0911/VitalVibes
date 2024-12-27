package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitalvibes.Adapter.UserListAdapter;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.databinding.ActivityManageUserBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class ManageUser extends AppCompatActivity implements UserListAdapter.OnDeleteUserListener {
    private ChipNavigationBar chipNavigationBar;
    private ArrayList<Donor> donorsList = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private ActivityManageUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyleView();

        String userId;
        // Initialize Firebase Database
        databaseReference = firebaseDatabase.getReference("Donors");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            userId = currentUser.getUid();
            // Fetch user role from the database
            try {
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
                        Toast.makeText(ManageUser.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(ManageUser.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        setUpChipNavigationBar();
        fetchDonorData();
    }

    private void fetchDonorData() {
        binding.progressBarUser.setVisibility(View.VISIBLE);

        DatabaseReference myDonor = FirebaseDatabase.getInstance().getReference("Donors");
        try {
            myDonor.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    donorsList.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot issues : snapshot.getChildren()){
                            Donor donor = issues.getValue(Donor.class);
                            if (donor != null){
                                donorsList.add(donor);
                            }
                        }
                        userListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ManageUser.this, "No User available.", Toast.LENGTH_SHORT).show();
                    }
                    binding.progressBarUser.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageUser.this, "Error fetching donor.", Toast.LENGTH_SHORT).show();
                    binding.progressBarUser.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(ManageUser.this, "Error fetching donor data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarUser.setVisibility(View.GONE);
        }
    }

    private void setupRecyleView() {
        userListAdapter = new UserListAdapter(this, donorsList, this);
        binding.UserRecyleView.setLayoutManager(new LinearLayoutManager(this));
        binding.UserRecyleView.setAdapter(userListAdapter);
    }

    private void navigateToActivity(Class<?> targetActivity) {
        try {
            startActivity(new Intent(ManageUser.this, targetActivity));
        } catch (Exception e) {
            Toast.makeText(ManageUser.this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpChipNavigationBar() {
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);

        // Set the default selected item and attach a listener
        chipNavigationBar.setItemSelected(R.id.users, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Class<?> targetActivity = null;

            // Determine target activity based on the selected menu item
            if (id == R.id.home) {
                targetActivity = HomeActivity.class;
            } else if (id == R.id.users) {
                targetActivity = ManageUser.class;
            }

            // Only navigate if the selected activity is different from the current one
            if (targetActivity != null && !targetActivity.equals(this.getClass())) {
                navigateToActivity(targetActivity);
            }
        });
    }

    private void displayRoleBasedUI(String role) {
        if ("Admin".equals(role)) {
            // Display Admin features
            showAdminFeatures();
        }
    }

    private void showAdminFeatures() {
        try {
            // Hide donor-specific UI elements and show admin-specific UI
            chipNavigationBar.findViewById(R.id.createSite).setVisibility(View.GONE);
            chipNavigationBar.findViewById(R.id.location).setVisibility(View.GONE);
            chipNavigationBar.findViewById(R.id.profile).setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(ManageUser.this, "Error displaying admin features: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteUser(String userId) {
        try {
            // Add logic for deleting a user
        } catch (Exception e) {
            Toast.makeText(ManageUser.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
