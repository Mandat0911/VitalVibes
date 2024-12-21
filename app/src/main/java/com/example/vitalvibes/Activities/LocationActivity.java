package com.example.vitalvibes.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vitalvibes.R;
import com.example.vitalvibes.databinding.ActivityLocationBinding;
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

        // Get last location
        getLastLocation();
        searchMap();
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
                if (map != null) {
                    LatLng myPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                    MarkerOptions options = new MarkerOptions().position(myPosition).title("My Location");
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    map.addMarker(options);
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
            map.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
            MarkerOptions options = new MarkerOptions().position(myPosition).title("My Location");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            map.addMarker(options);
        }
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
}
