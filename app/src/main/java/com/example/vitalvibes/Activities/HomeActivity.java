package com.example.vitalvibes.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private HosptalListAdapter adapter;
    private ArrayList<Hospital> hospitalsList = new ArrayList<>();
    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Donors");

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // If user is not logged in, redirect to login
            handleLogout();
            return;
        }

        initializeUI();
        fetchUserData(currentUser.getUid());
        fetchHospitalData();
        setupSearchView();
        setupChipNavigationBar();
    }

    private void initializeUI() {
        binding.Notification.setOnClickListener(v -> navigateToActivity(NotificationActivity.class));
        binding.progressBarCategory.setVisibility(View.GONE);
    }

    private void fetchUserData(String userId) {
        System.out.println("userid: " + userId);
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Donor donor = snapshot.getValue(Donor.class);
                    if (donor != null) {
                        userRole = donor.getRole();
                        binding.textHome.setText("Hi, " + donor.getName());
                        displayRoleBasedUI(userRole);
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No user data found.", Toast.LENGTH_SHORT).show();
                    handleLogout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchHospitalData() {
        DatabaseReference hospitalReference = firebaseDatabase.getReference("Hospital");
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        hospitalReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hospitalsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Hospital hospital = issue.getValue(Hospital.class);
                        if (hospital != null) {
                            hospitalsList.add(hospital);
                        }
                    }
                    setupRecyclerView();
                } else {
                    Toast.makeText(HomeActivity.this, "No hospital data available.", Toast.LENGTH_SHORT).show();
                }
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                binding.progressBarCategory.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new HosptalListAdapter(hospitalsList);
        binding.homeMainView.setLayoutManager(new LinearLayoutManager(this));
        binding.homeMainView.setAdapter(adapter);
    }

    private void setupSearchView() {
        SearchView searchView = binding.searchBar;
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return false;
            }
        });
    }

    private void setupChipNavigationBar() {
        chipNavigationBar = binding.chipNavigationBar;
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);
        chipNavigationBar.setItemSelected(R.id.home, true);

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Class<?> targetActivity = null;

            if (id == R.id.home) {
                targetActivity = HomeActivity.class;
            } else if (id == R.id.createSite) {
                targetActivity = CreateSiteActivity.class;
            } else if (id == R.id.location) {
                targetActivity = LocationActivity.class;
            } else if (id == R.id.profile) {
                targetActivity = ProfileActivity.class;
            }

            if (targetActivity != null && !targetActivity.equals(this.getClass())) {
                navigateToActivity(targetActivity);
            }
        });
    }

    private void displayRoleBasedUI(String role) {
        if ("Admin".equals(role)) {
            showAdminFeatures();
        } else {
            initCategory();
        }
    }

    private void initCategory() {
        fetchHospitalData();
    }

    private void showAdminFeatures() {
        binding.HomeGreeting.setVisibility(View.GONE);
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(HomeActivity.this, targetActivity));
    }

    private void handleLogout() {
        Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
