package com.example.vitalvibes.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityLocationBinding;
import com.example.vitalvibes.model.Donor;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    ChipNavigationBar chipNavigationBar;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String userRole;
    private ActivityLocationBinding binding;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ChipNavigationBar
        chipNavigationBar = findViewById(R.id.chipNavigationBarLocation);
        setUpChipNavigationBar();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize MapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Initialize the map
        }
        String location = getIntent().getStringExtra("address");
        if (location != null && !location.isEmpty()) {
            // Call searchMap directly with the address
            binding.mapSearch.setQuery(location, false);
            searchMapFromHospital(location);
        }
        String userId;
        // Initialize Firebase Database
        databaseReference = firebaseDatabase.getReference("Donors");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            userId = currentUser.getUid();
            // Fetch user role from the database
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.exists()) {
                            Donor donor = dataSnapshot.getValue(Donor.class);
                            if (donor != null) {
                                userRole = donor.getRole();  // Store the role
                                displayRoleBasedUI(userRole);
                            }
                        }
                    }catch (Exception e) {
                        Toast.makeText(LocationActivity.this, "Error processing user data", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LocationActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Set up listener for the button to open Google Maps
        binding.btnOpenGoogleMaps.setOnClickListener(v -> openInGoogleMaps());

        // Get last location
        getLastLocation();
        searchMap();
    }

    private void openInGoogleMaps() {
        String location = binding.mapSearch.getQuery().toString();

        if (location != null && !location.isEmpty()) {
            // Create an Intent to open Google Maps
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            try {
                // Check if Google Maps is installed and start the activity
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // If Google Maps is not installed, show a message
                    Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }
            }catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Unable to open Google Maps. Please install the app.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Unexpected error occurred while opening Google Maps", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please enter a valid location", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchMapFromHospital(String location) {
        AtomicReference<List<Address>> addressList = new AtomicReference<>();

        if (location != null && !location.isEmpty()) {
            Geocoder geocoder = new Geocoder(LocationActivity.this);

            new Thread(() -> {
                try {
                    addressList.set(geocoder.getFromLocationName(location, 1));
                    if (addressList.get() != null && !addressList.get().isEmpty()) {
                        Address address = addressList.get().get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        runOnUiThread(() -> {
                            MarkerOptions options = new MarkerOptions().position(latLng).title(location);
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            map.addMarker(options);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(LocationActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private void searchMap() {
        binding.mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = binding.mapSearch.getQuery().toString();
                AtomicReference<List<Address>> addressList = new AtomicReference<>();

                if (location != null && !location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(LocationActivity.this);

                    new Thread(() -> {
                        try {
                            addressList.set(geocoder.getFromLocationName(location, 1));
                            if (addressList.get() != null && !addressList.get().isEmpty()) {
                                Address address = addressList.get().get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                                runOnUiThread(() -> {
                                    MarkerOptions options = new MarkerOptions().position(latLng).title(location);
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    map.addMarker(options);
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(LocationActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getLastLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }

        // Get last known location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                try {
                    if (map != null) {
                        LatLng myPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                        MarkerOptions options = new MarkerOptions().position(myPosition).title("My Location");
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        map.addMarker(options);
                    }
                }catch (Exception e) {
                    Toast.makeText(this, "Error displaying location on the map", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(LocationActivity.this, targetActivity));
    }

    private void setUpChipNavigationBar() {
        chipNavigationBar.setMenuResource(R.menu.menu_bottom);
        chipNavigationBar.setItemSelected(R.id.location, true); // Set default selected item

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Add a marker at current location if available
        if (currentLocation != null) {
            LatLng myPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15)); // Set zoom level

            MarkerOptions options = new MarkerOptions()
                    .position(myPosition)
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(options);
        }

        // Enable UI settings
        map.getUiSettings().setZoomControlsEnabled(true); // Show zoom buttons
        map.getUiSettings().setCompassEnabled(true); // Enable compass
        map.getUiSettings().setZoomGesturesEnabled(true); // Enable pinch-to-zoom gestures
        map.setPadding(50, 100, 50, 200); // Left, Top, Right, Bottom padding

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
                // Optionally, redirect to settings if permission is permanently denied
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mapNormal){
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (id == R.id.mapSatellite){
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        if (id == R.id.mapHybrid){
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        if (id == R.id.mapTerrain){
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayRoleBasedUI(String role) {
        try {
            if ("Donor".equals(role)) {
                // Display Admin features
                showDonorFeature();
            }
        }catch (Exception e) {
            Log.e("UIError", "Error displaying UI for role: " + e.getMessage());
            Toast.makeText(LocationActivity.this, "Error updating UI.", Toast.LENGTH_SHORT).show();
        }

    }
    private void showDonorFeature() {
        chipNavigationBar.findViewById(R.id.users).setVisibility(View.GONE);
    }
}
