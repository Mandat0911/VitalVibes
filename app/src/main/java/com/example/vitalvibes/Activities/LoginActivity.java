package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vitalvibes.databinding.ActivityLoginBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    private ActivityLoginBinding binding;
    @Override
    public void onStart(){

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            checkUserID(currentUser.getUid());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
        // Set up register button click listener (optional)
        binding.HaveNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class); // Assume RegisterActivity is the registration screen
            startActivity(intent);
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Donors");

        binding.LoginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = binding.LoginEmailInput.getText().toString().trim();
        String password = binding.loginPasswordInput.getText().toString().trim();

        if (validateInputs(email, password)) {
            // Check if the email exists and the password matches
            checkUserCredentials(email, password);
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.LoginEmailInput.setError("Invalid email format");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            binding.loginPasswordInput.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void checkUserCredentials(String email, String password) {
        try {
            // Query the database for the user's email
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                checkUserID(user.getUid());
                                Toast.makeText(LoginActivity.this, "Logged in successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Error checking credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e) {
            Toast.makeText(this, "Unexpected error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserID(String userId) {
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Donor donor = snapshot.getValue(Donor.class);
                    if (donor != null) {
                        Intent intent;
                        if ("Donor".equalsIgnoreCase(donor.getRole())) {
                            // Redirect admin to Admin Dashboard
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        } else {
                            // Redirect user to Home
                            intent = new Intent(LoginActivity.this, AllUser.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}