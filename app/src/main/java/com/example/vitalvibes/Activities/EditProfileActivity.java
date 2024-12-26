package com.example.vitalvibes.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vitalvibes.databinding.ActivityEditProfileBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Donors").child(userId);

        fetchProfileData();

        // Save changes when the update button is clicked
        binding.SaveBtn.setOnClickListener(v -> updateProfileData());
        setUpListener();

        binding.updateBloodType.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Blood Type");
            String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
            builder.setItems(bloodTypes, (dialog, which) -> {
                // Set the selected blood type in the EditText
                binding.updateBloodType.setText(bloodTypes[which]);
            });
            builder.show();
        });

        binding.updateDOBInput.setOnClickListener(v -> {
            openDialog();
        });
    }

    private void openDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH); // Zero-based (0 = January)
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize the DatePickerDialog with the current date as the default
        @SuppressLint("SetTextI18n")
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Format the selected date
            String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            binding.updateDOBInput.setText(formattedDate);
        }, currentYear, currentMonth, currentDay);

        dialog.show();
    }

    private void fetchProfileData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Donor donor = snapshot.getValue(Donor.class);
                    if (donor != null) {
                        // Populate data into fields
                        binding.signUpFullName.setText(donor.getName());
                        binding.signUpEmailInput.setText(donor.getEmail());
                        binding.updateDOBInput.setText(donor.getDob());
                        binding.updateBloodType.setText(donor.getBloodType());
                        binding.signUpMobileInput.setText(donor.getPhoneNumber());
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileData() {
        // Get updated values
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get user ID from Firebase
        String name = binding.signUpFullName.getText().toString().trim();
        String email = binding.signUpEmailInput.getText().toString().trim();
        String dob = binding.updateDOBInput.getText().toString().trim();
        String phoneNumber = binding.signUpMobileInput.getText().toString().trim();
        String bloodType = binding.updateBloodType.getText().toString().trim();
        String password = binding.signUpPasswordInput.getText().toString().trim();
        String confirmPassword = binding.signUpConfirmPasswordInput.getText().toString().trim();
        String role = "Donor"; // Assume role is fixed, or fetch dynamically if required

        // Validate fields
        if (name.isEmpty() || email.isEmpty() || dob.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Hash the password if provided
        String hashedPassword = null;
        if (!password.isEmpty()) {
            hashedPassword = hashPassword(password);
        } else {
            hashedPassword = ""; // Default to empty string if no password is provided
        }

        // Create updated Donor object
        Donor updatedDonor = new Donor(userId, name, email, dob, phoneNumber, hashedPassword, bloodType, role);

        // Reference to the Firebase Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Donors").child(userId);

        // Update profile in Firebase Database
        databaseReference.setValue(updatedDonor).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(EditProfileActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Password hashing method remains unchanged
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpListener() {
        binding.backBtnDetailEdit.setOnClickListener(v -> navigateToActivity(ProfileActivity.class));
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(EditProfileActivity.this, targetActivity);
        // Add flags to clear the back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}