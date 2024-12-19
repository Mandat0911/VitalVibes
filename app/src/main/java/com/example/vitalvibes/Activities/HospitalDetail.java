package com.example.vitalvibes.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityHomeBinding;
import com.example.vitalvibes.databinding.ActivityHospitalDetailBinding;
import com.example.vitalvibes.model.Hospital;

public class HospitalDetail extends AppCompatActivity {
    private ActivityHospitalDetailBinding binding;
    private Hospital object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable(){
        binding.TitleDetail.setText(object.getName());
        binding.phoneDetail.setText(object.getMobile());
        binding.descriptionDetail.setText(object.getHospitalBio());
        binding.AddressDetail.setText(object.getAddress());
        binding.backBtnDetail.setOnClickListener(v -> finish());
        binding.siteDetail.setText(object.getSite());
        binding.distanceDetail.setText(object.getNearest());
        binding.ratingNumDetail.setText(object.getRating()+" Rating");
        binding.ratingBarDetail.setRating( object.getRating());
        binding.durationDetail.setText(object.getDate());
    }

    private void getIntentExtra(){
        object = (Hospital) getIntent().getSerializableExtra("object");
    }
}