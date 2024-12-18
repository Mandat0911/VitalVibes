package com.example.vitalvibes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitalvibes.Activities.LoginActivity;
import com.example.vitalvibes.Activities.SignupActivity;
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