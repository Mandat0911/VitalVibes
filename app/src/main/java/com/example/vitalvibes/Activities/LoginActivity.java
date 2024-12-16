package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityLoginBinding;
import com.example.vitalvibes.databinding.ActivityMainBinding;
import com.example.vitalvibes.model.Donor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        // Query the database for the user's email
        databaseReference.orderByChild("email").equalTo(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            // Email exists, check the password
                            Donor donor = snapshot.getChildren().iterator().next().getValue(Donor.class);
                            if (donor != null && donor.getPassword().equals(hashPassword(password))) {
                                // Correct password, check the role and navigate
                                Intent intent;
                                intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(LoginActivity.this, "Logged in successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                binding.loginPasswordInput.setError("Incorrect password");
                            }
                        } else {
                            binding.LoginEmailInput.setError("Email not registered");
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Error checking credentials", Toast.LENGTH_SHORT).show();
                    }
                });
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