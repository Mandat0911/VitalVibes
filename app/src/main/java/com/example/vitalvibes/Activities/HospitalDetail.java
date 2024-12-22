package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.vitalvibes.Adapter.PicListAdapter;
import com.example.vitalvibes.databinding.ActivityHospitalDetailBinding;
import com.example.vitalvibes.model.Hospital;

import java.util.ArrayList;

public class HospitalDetail extends AppCompatActivity {
    private ActivityHospitalDetailBinding binding;
    private Hospital object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inside HospitalDetail activity's onCreate method
        binding.ShowMapDetailBtn.setOnClickListener(v -> {
            String address = object.getAddress(); // Get address from the Hospital object
            if (address != null && !address.isEmpty()) {
                Intent intent = new Intent(HospitalDetail.this, LocationActivity.class);
                intent.putExtra("address", address); // Pass the address to LocationActivity
                startActivity(intent);
            } else {
                Toast.makeText(HospitalDetail.this, "Address is not available", Toast.LENGTH_SHORT).show();
            }
        });

        getIntentExtra();
        setVariable();
        initList();
    }

    private void setVariable(){
        binding.TitleDetail.setText(object.getName());
        binding.phoneDetail.setText(object.getMobile());
        binding.descriptionDetail.setText(object.getHospitalBio());
        binding.AddressDetail.setText(object.getAddress());
        binding.backBtnDetail.setOnClickListener(v -> finish());
        binding.siteDetail.setText(object.getSite());
        binding.distanceDetail.setText(object.getlocation());
        binding.ratingNumDetail.setText(object.getRating()+" Rating");
        binding.ratingBarDetail.setRating( object.getRating());
        binding.durationDetail.setText(object.getDate());
    }

    private void initList() {
        ArrayList<String> picList = new ArrayList<>(object.getPic());

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.mainPic);

        binding.picListDetail.setAdapter(new PicListAdapter(picList, binding.mainPic));
        binding.picListDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
    }

    private void getIntentExtra(){
        object = (Hospital) getIntent().getSerializableExtra("object");
    }
}