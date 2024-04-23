package com.android.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.android.locationtracker.databinding.ActivityCurrentLocationBinding;
import com.android.locationtracker.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class current_location extends AppCompatActivity  {
    ActivityCurrentLocationBinding binding;
    boolean autoUpdate = false;
    Location currentLocation;

    public static final int FAST_UPDATE_REQUEST = 50;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int PERMISSION_FINE_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrentLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkLocationPermission();
        locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValue(locationResult.getLastLocation());
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_REQUEST);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        binding.gpsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.gpsSwitch.isChecked()) {
                    // Most accurate use of GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    binding.sensorTxt.setText("Sensor: Using GPS Sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    binding.sensorTxt.setText("Sensor: Using Tower + WiFi");
                }
            }
        });


        binding.locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.locationSwitch.isChecked()) {
                    //turn on tracking
                    startTracking();
                } else {
                    stopTracking();
                }
            }
        });

        updateGPS();
    }

    private void updateGPS() {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            // Permission is already granted, so proceed with location retrieval
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(current_location.this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Handle the location data (e.g., update UI textViews)
                        updateUIValue(location);
                        currentLocation = location;

                    } else {
                        // Location is null, handle the case where no location is available
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
                        }
                    }
                }
            });
        }
    }

    // methods to update the values when the user will click on buttons
    @SuppressLint({"SetTextI18n", "MissingPermission"})
    private void startTracking() {
        //this method start GPS tracking
        binding.trackingStatus.setText("Tracking Status: Location tracking is start.");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();

    }

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    private void stopTracking() {
        //this method will stop the GPS tracking
        binding.latitudeTxt.setText("Latitude: Not Available Now");
        binding.longitudeTxt.setText("Longitude: Not Available Now");
        binding.accuracyTxt.setText("Accuracy: Not Available Now");
        binding.sensorTxt.setText("Sensor Type: Not Available now");
        binding.trackingStatus.setText("Tracking Status: Not Available");
        binding.addressTxt.setText("Address: Not Available");
        binding.altitudeTxt.setText("Altitude: Not Available Now");
        binding.trackingStatus.setText("Tracking Status: Location tracking is not start.");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    //method to update the Value of UI

    @SuppressLint("SetTextI18n")
    private void updateUIValue(Location location) {
        binding.latitudeTxt.setText("Latitude: " + String.valueOf(location.getLatitude()));
        binding.longitudeTxt.setText("Longitude: " + String.valueOf(location.getLongitude()));
        binding.accuracyTxt.setText("Accuracy: " + String.valueOf(location.getAccuracy()));

        //check that the device have the ability to have altitude
        if (location.hasAltitude()){
            binding.altitudeTxt.setText("Altitude: " + String.valueOf(location.getAltitude()));
        }
        else {
            binding.altitudeTxt.setText("Altitude: Not Available Now");
        }

        //check the speed
        if (location.hasSpeed()) {
            binding.speedTxt.setText("Speed: " + String.valueOf(location.getSpeed()));
        }
        else {
            binding.speedTxt.setText("Speed: Not Available Now");
        }

        //Update the value of MapView
        Geocoder geocoder = new Geocoder(current_location.this);
        try{
            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            binding.addressTxt.setText("Address: "+address.get(0).getAddressLine(0));
        }
        catch(Exception e){
            binding.addressTxt.setText("Address: unable to fetch Address");
        }

        binding.showMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(current_location.this, CurrentMapsActivity.class);
                // Pass latitude and longitude as extras
                double latitude = Double.valueOf(location.getLatitude()); // Replace with your actual latitude
                double longitude = Double.valueOf(location.getLongitude()); // Replace with your actual longitude
                // Reverse geocode the coordinates to get the address
                String address = getAddressFromCoordinates(latitude, longitude);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Address", address);
                startActivity(intent);


            }
        });


    }//end of the updateUIValues Method

    // Method to get the address from latitude and longitude using Geocoder
    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(current_location.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address";
    }

    //Method for check the permission to track location
    private  void  checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // If permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else {
            // Permission is already granted, so proceed with location retrieval

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(current_location.this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Handle the location data (e.g., update UI textViews)
                        updateUIValue(location);
                        currentLocation = location;

                    } else {
                        // Location is null, handle the case where no location is available
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_FINE_LOCATION);
                        }
                    }
                }
            });
        }

    }


}