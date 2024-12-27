package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vitalvibes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    // Define constants for menu IDs
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpListener();

    }

    private void setUpListener() {
        binding.welcomeLoginButton.setOnClickListener(v -> navigateToActivity(LoginActivity.class));
        binding.WelcomeSignUpButton.setOnClickListener(v -> navigateToActivity(SignupActivity.class));
    }

    private void navigateToActivity(Class<?> targetActivity) {
        try {
            Intent intent = new Intent(MainActivity.this, targetActivity);
            // Add flags to clear the back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Ensure the current activity is finished
        }catch (Exception e) {
            Log.e("NavigationError", "Error navigating to activity: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Unable to open activity.", Toast.LENGTH_SHORT).show();
        }
    }
}