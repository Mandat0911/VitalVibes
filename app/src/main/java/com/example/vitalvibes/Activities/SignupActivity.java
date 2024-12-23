package com.example.vitalvibes.Activities;

import static com.example.vitalvibes.Utils.Utils.isValidDob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vitalvibes.databinding.ActivitySignupBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding; // Assuming you use View Binding
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;  // FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donors");

        // Set up the sign-up button
        binding.SignUpButton.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String name = binding.signUpFullName.getText().toString().trim();
        String email = binding.signUpEmailInput.getText().toString().trim();
        String dob = binding.signUpDOBInput.getText().toString().trim();
        String phoneNumber = binding.signUpMobileInput.getText().toString().trim();
        String password = binding.signUpPasswordInput.getText().toString().trim();
        String confirmPassword = binding.signUpConfirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (validateInputs(name, email, dob, phoneNumber, password, confirmPassword)) {
            // Check if email exists in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Firebase Authentication successful, create user in Firebase Database
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            assert firebaseUser != null;
                            firebaseUser.sendEmailVerification();
                            String userId = firebaseUser.getUid();  // Get Firebase UID

                            // Hash the password (optional, you can store password as is)
                            String hashedPassword = hashPassword(password);

                            // Validate Date of Birth
                            if (!isValidDob(dob)) {
                                Toast.makeText(this, "Invalid Date of Birth. Valid format is dd/mm/yyyy", Toast.LENGTH_SHORT).show();
                                return; // Early return if DOB is invalid
                            }

                            // Default role is "Donor"
                            String role = "Donor";

                            // Create Donor object
                            Donor donor = new Donor(userId, name, email, dob, phoneNumber, hashedPassword, role);

                            // Save the Donor object in Firebase Realtime Database
                            databaseReference.child(userId).setValue(donor)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                            // Redirect to login page
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Sign-up failed: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInputs(String name, String email, String dob, String phoneNumber, String password, String confirmPassword) {
        if (name.isEmpty()) {
            binding.signUpFullName.setError("Name cannot be empty");
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signUpEmailInput.setError("Invalid email format");
            return false;
        }
        if (dob.isEmpty()) {
            binding.signUpDOBInput.setError("Invalid date format (use dd-MM-yyyy)");
            return false;
        }
        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            binding.signUpMobileInput.setError("Invalid phone number");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            binding.signUpPasswordInput.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            binding.signUpConfirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        return true;
    }

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

}

