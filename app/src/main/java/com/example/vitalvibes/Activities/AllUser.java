package com.example.vitalvibes.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitalvibes.Adapter.UserListAdapter;
import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityAllUserBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AllUser extends AppCompatActivity implements UserListAdapter.OnDeleteUserListener {

    private ChipNavigationBar chipNavigationBar;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private ArrayList<Donor> donorsList = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private ActivityAllUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Initialize Firebase Database
        databaseReference = firebaseDatabase.getReference("Donors");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
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
                    Toast.makeText(AllUser.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Request storage permission if not already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }

        // Set up export button
        binding.UserExpertBtn.setOnClickListener(v -> exportDonorData());

        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        setUpChipNavigationBar();
        setupRecylerView();
        fetchDonorData();
    }

    private void exportDonorData() {
        if (donorsList.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use app-specific directory on external storage
        File exportDir = new File(getExternalFilesDir(null), "DonorData");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "Donors.csv");
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("ID,Name,Email,Phone,Role\n");

            // Write donor data
            for (Donor donor : donorsList) {
                writer.append(donor.getUserId()).append(",")
                        .append(donor.getName()).append(",")
                        .append(donor.getEmail()).append(",")
                        .append(donor.getPhoneNumber()).append(",")
                        .append(donor.getRole()).append("\n");
            }

            writer.flush();
            Toast.makeText(this, "Data exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Optionally, share the file
            shareExportedFile(file);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareExportedFile(File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Donor Data Export");
        startActivity(Intent.createChooser(intent, "Share Donor Data"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void fetchDonorData() {
        binding.progressBarUser.setVisibility(View.VISIBLE);

        DatabaseReference myDonor = FirebaseDatabase.getInstance().getReference("Donors");
        myDonor.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donorsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot issues : snapshot.getChildren()) {
                        Donor donor = issues.getValue(Donor.class);
                        if (donor != null && !"Admin".equals(donor.getRole())) {
                            donorsList.add(donor);
                        }
                    }
                    userListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AllUser.this, "No Users available.", Toast.LENGTH_SHORT).show();
                }
                binding.progressBarUser.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllUser.this, "Error fetching donor.", Toast.LENGTH_SHORT).show();
                binding.progressBarUser.setVisibility(View.GONE);
            }
        });
    }

    private void setupRecylerView() {
        userListAdapter = new UserListAdapter(this, donorsList, this); // Pass 'this' as the listener

        binding.UserRecyleView.setLayoutManager(new LinearLayoutManager(this));
        binding.UserRecyleView.setAdapter(userListAdapter);
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(AllUser.this, targetActivity));
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
                targetActivity = AllUser.class;
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
        // Hide donor-specific UI elements and show admin-specific UI
        // Set the default selected item and attach a listener
        chipNavigationBar.findViewById(R.id.createSite).setVisibility(View.GONE);
        chipNavigationBar.findViewById(R.id.location).setVisibility(View.GONE);
        chipNavigationBar.findViewById(R.id.profile).setVisibility(View.GONE);
    }

    @Override
    public void onDeleteUser(String userId) {
        // Handle the deletion of user here
        deleteUser(userId);
    }

    private void deleteUser(String userId) {
        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Donors");

        donorRef.child(userId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AllUser.this, "Delete user successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AllUser.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}