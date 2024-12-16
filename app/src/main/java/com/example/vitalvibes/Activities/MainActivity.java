package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vitalvibes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

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
        startActivity(new Intent(MainActivity.this, targetActivity));
    }
}