package com.example.vitalvibes.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.vitalvibes.databinding.ActivityCreateSiteBinding;
import com.example.vitalvibes.model.Hospital;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateSiteActivity extends AppCompatActivity {
    private Calendar startDate;
    private static final String CLOUD_API_KEY = "541771626282227";
    private static final String CLOUD_NAME = "dcc6xt2mw";
    private static final String CLOUD_API_SECRET = "4RAEy_XPHb8bvQGlu5HJ4bCOuwg";
    private static final String TAG = "Upload";
    private static final int IMAGE_REQ = 1;
    private static int selectedImageSlot = 0;  // Track which image slot is selected (1, 2, or 3)

    private final ArrayList<Uri> imagePath = new ArrayList<>();
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;  // FirebaseAuth instance

    private ActivityCreateSiteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateSiteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Hospital");

        initConfig();
        setListener();
        setupImageClickListeners();
    }

    private void createSite() {
        String siteName = binding.SiteName.getText().toString().trim();
        String address = binding.SiteAddress.getText().toString().trim();
        String startDay = binding.SiteStartDay.getText().toString().trim();
        String phoneNumber = binding.SitePhone.getText().toString().trim();
        String endDay = binding.SiteEndDay.getText().toString().trim();
        String bio = binding.SiteDescription.getText().toString().trim();

        // Get the current user's UID (owner)
        String ownerUID = mAuth.getCurrentUser().getUid();


        if (siteName.isEmpty() || address.isEmpty() || startDay.isEmpty() || phoneNumber.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImages(siteName, address, startDay, phoneNumber, endDay, bio, ownerUID);
    }

    private void initConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", CLOUD_API_KEY);
        config.put("api_secret", CLOUD_API_SECRET);
        config.put("secure", "true");
        MediaManager.init(this, config);
    }

    private void setListener() {
        binding.backBtnCreateSite.setOnClickListener(v -> finish());
        binding.CreateSiteBtn.setOnClickListener(v -> createSite());
        binding.SiteStartDay.setOnClickListener(v -> openDialog(binding.SiteStartDay, null));
        binding.SiteEndDay.setOnClickListener(v -> {
            // Pass the selected start date to ensure end date is always after start date
            String startDateString = binding.SiteStartDay.getText().toString();
            openDialog(binding.SiteEndDay, startDateString);
        });
    }

    private void setupImageClickListeners() {
        binding.image1.setOnClickListener(v -> {
            selectedImageSlot = 1;
            requestPermissions();
        });
        binding.image2.setOnClickListener(v -> {
            selectedImageSlot = 2;
            requestPermissions();
        });
        binding.image3.setOnClickListener(v -> {
            selectedImageSlot = 3;
            requestPermissions();
        });
    }

    private void requestPermissions() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? android.Manifest.permission.READ_MEDIA_IMAGES
                : android.Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, IMAGE_REQ);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied to access images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selection
        startActivityForResult(intent, IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Multiple images selected
                    Uri imageUri = data.getClipData().getItemAt(0).getUri(); // Pick the first image for simplicity
                    imagePath.add(imageUri); // Add the image to the list
                    displaySelectedImage(imageUri);
                } else if (data.getData() != null) { // Single image selected
                    Uri imageUri = data.getData();
                    imagePath.add(imageUri); // Add the image to the list
                    displaySelectedImage(imageUri);
                }
            }
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        // Display image in corresponding slot based on selectedImageSlot
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

    private void uploadImages(String siteName, String address, String startDay, String phoneNumber, String endDay, String bio, String ownerUID) {
        if (imagePath.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> imageUrls = new ArrayList<>();
        for (Uri imageUri : imagePath) {
            MediaManager.get().upload(imageUri).callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.d(TAG, "onStart: Upload started for " + requestId);
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.d(TAG, "onProgress: Uploading " + requestId);
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String imageUrl = (String) resultData.get("secure_url");
                    imageUrls.add(imageUrl); // Add the uploaded image URL to the list
                    if (imageUrls.size() == imagePath.size()) {
                        saveSiteData(siteName, address, startDay, phoneNumber, endDay, bio, imageUrls, ownerUID);
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onError: Upload failed for " + requestId + " with error: " + error.getDescription());
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.d(TAG, "onReschedule: Upload rescheduled for " + requestId);
                }
            }).dispatch();
        }
    }

    private void saveSiteData(String siteName, String address, String startDay, String mobile, String endDay, String bio, ArrayList<String> imageUrls, String ownerUID) {
        String hospitalId = databaseReference.push().getKey(); // Generate unique hospital ID
        if (hospitalId != null) {
            // Create a Hospital object with the data
            Hospital hospital = new Hospital(hospitalId,siteName, address, bio, mobile, imageUrls, startDay, endDay, ownerUID);

            // Save site data to Firebase Realtime Database
            databaseReference.child(hospitalId).setValue(hospital)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateSiteActivity.this, "Site created successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after successful creation
                        } else {
                            Toast.makeText(CreateSiteActivity.this, "Failed to create site", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void openDialog(TextView targetView, String minDateString) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        if (minDateString != null) {
            // Parse the start date string (e.g., "15/12/2024")
            String[] dateParts = minDateString.split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Month is zero-based
            int year = Integer.parseInt(dateParts[2]);

            // Set the calendar to the selected start date
            calendar.set(year, month, day);
        }

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH); // Zero-based (0 = January)
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize the DatePickerDialog
        @SuppressLint("SetTextI18n")
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Format the selected date
            String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            targetView.setText(formattedDate);
        }, currentYear, currentMonth, currentDay);

        if (minDateString != null) {
            // Set the minimum date for the "End Day"
            dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }

        dialog.show();
    }

}
