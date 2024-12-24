package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HospitalDetail extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ActivityHospitalDetailBinding binding;
    private Hospital object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Hospital");

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

        binding.EditSiteBtn.setOnClickListener(v -> {
            String hospitalId = object.getHospitalId(); // Get address from the Hospital object
            if (hospitalId != null && !hospitalId.isEmpty()) {
                Intent intent = new Intent(HospitalDetail.this, EditSiteActivity.class);
                intent.putExtra("hospitalId", hospitalId); // Pass the address to LocationActivity
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
        binding.TitleDetail.setText(object.getSiteName());
        binding.phoneDetail.setText(object.getMobile());
        binding.descriptionDetail.setText(object.getHospitalBio());
        binding.AddressDetail.setText(object.getAddress());
        binding.backBtnDetail.setOnClickListener(v -> finish());

        binding.distanceDetail.setText(object.getAddress());
        // Check if the current user is the owner
        String ownerUID = object.getOwnerUID(); // Get ownerUID from the hospital object
        System.out.println(ownerUID);
        String currentUserUID = mAuth.getCurrentUser().getUid(); // Get current user UID
        System.out.println(currentUserUID);
        String hospitalId = object.getHospitalId(); // Get the hospital ID from the object
        System.out.println(hospitalId);

        if (currentUserUID.equals(ownerUID)) {
            // Show the delete button only if the current user is the owner
            binding.DeleteSiteBtn.setVisibility(View.VISIBLE);
            binding.EditSiteBtn.setVisibility(View.VISIBLE);
            binding.DonorSiteBtn.setVisibility(View.GONE);

            // Handle delete button click
            binding.DeleteSiteBtn.setOnClickListener(v -> deleteSite());
        } else {
            // Hide the delete button if the current user is not the owner
            binding.DeleteSiteBtn.setVisibility(View.GONE);
            binding.EditSiteBtn.setVisibility(View.GONE);
            binding.DonorSiteBtn.setVisibility(View.VISIBLE);
        }
    }

    // Method to delete the site
    private void deleteSite() {
        String hospitalId = object.getHospitalId(); // Get the hospital ID from the object
        System.out.println(hospitalId);
        databaseReference.child(hospitalId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(HospitalDetail.this, "Site deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    } else {
                        Toast.makeText(HospitalDetail.this, "Failed to delete site", Toast.LENGTH_SHORT).show();
                    }
                });
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