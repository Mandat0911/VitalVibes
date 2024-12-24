package com.example.vitalvibes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vitalvibes.Adapter.HosptalListAdapter;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.model.Donor;
import com.example.vitalvibes.model.Hospital;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChipNavigationBar chipNavigationBar;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private ActivityHomeBinding binding;
    private SearchView searchView;
    private HosptalListAdapter adapter; // Adapter for RecyclerView
    private ArrayList<Hospital> hospitalsList = new ArrayList<>(); // List of hospitals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database
        databaseReference = firebaseDatabase.getReference("Donors");
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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

        // Initialize ChipNavigationBar
        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        setUpChipNavigationBar();
        recyclerView = findViewById(R.id.homeMainView);  // Your RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the SearchView
        searchView = findViewById(R.id.search_bar);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No action when submitting the query
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the hospital list based on the query text
                //adapter.filter(newText);
                return false;
            }
        });
        fetchHospitalData();
        fetchDonorData();
    }

    private void fetchDonorData() {
        // Get the FirebaseAuth instance
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Ensure the user is logged in
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(HomeActivity.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the specific donor's data using their userId
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Donors").child(userId);

        // Fetch data for the authenticated user
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Map the snapshot to a Donor object
                    Donor user = snapshot.getValue(Donor.class);

                    // Check if the user object and its name are not null
                    if (user != null && user.getName() != null) {
                        // Display the donor's name in the text view
                        binding.textHome.setText("Hi, " +user.getName());
                    } else {
                        // Handle missing name or data
                        Toast.makeText(HomeActivity.this, "Donor data is incomplete.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle case where no data exists for the user
                    Toast.makeText(HomeActivity.this, "No donor data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase database error
                Toast.makeText(HomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchHospitalData() {
        DatabaseReference myCategory = firebaseDatabase.getReference("Hospital");
        myCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Hospital hospital = issue.getValue(Hospital.class);
                        if (hospital != null) {
                            hospitalsList.add(hospital);
                        }
                    }
                    adapter = new HosptalListAdapter(hospitalsList);  // Set the adapter
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(HomeActivity.this, targetActivity));
    }

    private void setUpChipNavigationBar() {
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);

        // Set the default selected item and attach a listener
        chipNavigationBar.setItemSelected(R.id.home, true);

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

    private void initCategory() {
        DatabaseReference myCategory = firebaseDatabase.getReference("Hospital");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        hospitalsList.clear(); // Clear the list before adding new data

        myCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Hospital hospital = issue.getValue(Hospital.class);
                        if (hospital != null) {
                            hospitalsList.add(hospital); // Add hospital to list
                        }
                    }
                    if (!hospitalsList.isEmpty()) {
                        // Create the adapter and set it to the RecyclerView
                        adapter = new HosptalListAdapter(hospitalsList);
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
}
