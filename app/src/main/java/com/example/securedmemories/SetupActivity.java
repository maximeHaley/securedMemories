package com.example.securedmemories;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;



import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetupActivity extends AppCompatActivity {

    public EditText editAdresse;
    public Button buttonValider;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private double lat;
    private double lon;
    private LocationCallback locationCallback;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editAdresse = findViewById(R.id.adress);
        buttonValider = findViewById(R.id.buttonValidate);
        buttonValider.setOnClickListener(v->{
            String adresse = editAdresse.getText().toString();
            if(adresse.isEmpty()){
                Toast.makeText(this,"Veuillez entrer une adresse", Toast.LENGTH_SHORT).show();
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try{
                List<Address> result = geocoder.getFromLocationName(adresse,1);
                if(result!=null && !result.isEmpty()){
                    lat = result.get(0).getLatitude();
                    lon = result.get(0).getLongitude();
                    Intent intent = new Intent(this,MapActivity.class);
                    intent.putExtra("latitude", lat);
                    intent.putExtra("longitude", lon);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this,"Adresse introuvable",Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException e) {
                Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
        Button gpsButton = findViewById(R.id.buttonGetLocation);
        gpsButton.setOnClickListener(v -> {
            if (hasLocationPermission()) {
                requestFreshLocation();
                finish();
            } else {
                requestLocationPermissions();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestFreshLocation();
            } else {
                Toast.makeText(this, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestFreshLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setNumUpdates(1);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Intent intent = new Intent(SetupActivity.this, MapActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SetupActivity.this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }





    /*ça sert à la demande de permission de localisatione*/
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

}