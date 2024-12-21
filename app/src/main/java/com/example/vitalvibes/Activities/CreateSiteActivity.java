package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityCreateSiteBinding;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class CreateSiteActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    private ActivityCreateSiteBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize ChipNavigationBar
        chipNavigationBar = findViewById(R.id.chipNavigationBarCreateSite);
        setUpChipNavigationBar();

    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(CreateSiteActivity.this, targetActivity));
    }
    private void setUpChipNavigationBar() {
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);
        chipNavigationBar.setItemSelected(R.id.createSite, true); // Set default selected item

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Class<?> targetActivity = null;

            // Determine target activity based on the selected menu item
            if (id == R.id.home) {
                targetActivity = HomeActivity.class;
            } else if (id == R.id.createSite) {
                targetActivity = CreateSiteActivity.class;
            } else if (id == R.id.location) {
                targetActivity = LocationActivity.class;
            } else if (id == R.id.profile) {
                targetActivity = ProfileActivity.class;
            }

            // Only navigate if the selected activity is different from the current one
            if (targetActivity != null && !targetActivity.equals(this.getClass())) {
                navigateToActivity(targetActivity);
            }
        });
    }
}