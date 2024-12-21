package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityMainBinding;
import com.google.android.material.chip.Chip;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

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
        Intent intent = new Intent(MainActivity.this, targetActivity);
        // Add flags to clear the back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Ensure the current activity is finished
    }

}