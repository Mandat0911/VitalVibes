package com.example.vitalvibes.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.vitalvibes.databinding.ActivityEditSiteBinding;
import com.example.vitalvibes.model.Hospital;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditSiteActivity extends AppCompatActivity {
    private static final String TAG = "EditSite";
    private static final int IMAGE_REQ = 1;
    private static int selectedImageSlot = 0; // Track which image slot is selected (1, 2, or 3)
    private static final String CLOUD_API_KEY = "541771626282227";
    private static final String CLOUD_NAME = "dcc6xt2mw";
    private static final String CLOUD_API_SECRET = "4RAEy_XPHb8bvQGlu5HJ4bCOuwg";

    private ActivityEditSiteBinding binding;
    private DatabaseReference databaseReference;

    private String hospitalId; // The ID of the hospital to edit
    private Hospital currentHospital; // Store the current hospital data
    private final ArrayList<Uri> newImageUris = new ArrayList<>(); // New images selected by the user
    private final ArrayList<String> imageUrls = new ArrayList<>(); // Existing and new image URLs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Hospital");

        // Get the hospitalId from the intent
        hospitalId = getIntent().getStringExtra("hospitalId");

        if (hospitalId == null) {
            Toast.makeText(this, "Invalid site ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initConfig();
        loadSiteData();
        setupImageClickListeners();
        setListeners();
    }

    private void initConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", CLOUD_API_KEY);
        config.put("api_secret", CLOUD_API_SECRET);
        config.put("secure", "true");
        MediaManager.init(this, config);
    }

    private void loadSiteData() {
        databaseReference.child(hospitalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentHospital = snapshot.getValue(Hospital.class);
                if (currentHospital != null) {
                    populateUI();
                } else {
                    Toast.makeText(EditSiteActivity.this, "Failed to load site data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditSiteActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateUI() {
        binding.SiteName.setText(currentHospital.getSiteName());
        binding.SiteAddress.setText(currentHospital.getAddress());
        binding.SiteStartDay.setText(currentHospital.getStartDate());
        binding.SiteEndDay.setText(currentHospital.getEndDate());
        binding.SitePhone.setText(currentHospital.getMobile());
        binding.SiteDescription.setText(currentHospital.getHospitalBio());

        ArrayList<String> images = currentHospital.getPic();
        if (images.size() > 0)
            Picasso.get().load(images.get(0)).into(binding.image1);
        if (images.size() > 1)
            Picasso.get().load(images.get(1)).into(binding.image2);
        if (images.size() > 2)
            Picasso.get().load(images.get(2)).into(binding.image3);

        imageUrls.addAll(images);
    }


    private void setupImageClickListeners() {
        binding.image1.setOnClickListener(v -> {
            selectedImageSlot = 1;
            selectImage();
        });
        binding.image2.setOnClickListener(v -> {
            selectedImageSlot = 2;
            selectImage();
        });
        binding.image3.setOnClickListener(v -> {
            selectedImageSlot = 3;
            selectImage();
        });
    }

    private void setListeners() {
        binding.UpdateSiteBtn.setOnClickListener(v -> updateSite());
        binding.backBtnEditSite.setOnClickListener(v -> finish());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            newImageUris.add(imageUri); // Add the new image URI to the list

            switch (selectedImageSlot) {
                case 1:
                    binding.image1.setImageURI(imageUri);
                    break;
                case 2:
                    binding.image2.setImageURI(imageUri);
                    break;
                case 3:
                    binding.image3.setImageURI(imageUri);
                    break;
            }
        }
    }

    private void updateSite() {
        String siteName = binding.SiteName.getText().toString().trim();
        String address = binding.SiteAddress.getText().toString().trim();
        String startDay = binding.SiteStartDay.getText().toString().trim();
        String phoneNumber = binding.SitePhone.getText().toString().trim();
        String endDay = binding.SiteEndDay.getText().toString().trim();
        String bio = binding.SiteDescription.getText().toString().trim();

        if (siteName.isEmpty() || address.isEmpty() || startDay.isEmpty() || phoneNumber.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // If new images are selected, upload them first
        if (!newImageUris.isEmpty()) {
            uploadNewImages(siteName, address, startDay, phoneNumber, endDay, bio);
        } else {
            saveUpdatedData(siteName, address, startDay, phoneNumber, endDay, bio, imageUrls);
        }
    }

    private void uploadNewImages(String siteName, String address, String startDay, String phoneNumber, String endDay, String bio) {
        ArrayList<String> newImageUrls = new ArrayList<>();
        for (Uri imageUri : newImageUris) {
            MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {

                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {

                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String imageUrl = (String) resultData.get("secure_url");
                    newImageUrls.add(imageUrl);
                    if (newImageUrls.size() == newImageUris.size()) {
                        imageUrls.addAll(newImageUrls); // Add new images to the existing list
                        saveUpdatedData(siteName, address, startDay, phoneNumber, endDay, bio, imageUrls);
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Toast.makeText(EditSiteActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {

                }
            }).dispatch();
        }
    }

    private void saveUpdatedData(String siteName, String address, String startDay, String phoneNumber, String endDay, String bio, ArrayList<String> updatedImageUrls) {
        currentHospital.setSiteName(siteName);
        currentHospital.setAddress(address);
        currentHospital.setStartDate(startDay);
        currentHospital.setMobile(phoneNumber);
        currentHospital.setEndDate(endDay);
        currentHospital.setHospitalBio(bio);
        currentHospital.setPic(updatedImageUrls);

        databaseReference.child(hospitalId).setValue(currentHospital)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditSiteActivity.this, "Site updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditSiteActivity.this, "Failed to update site", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
