package com.example.securedmemories;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Initialisation globale*/
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textSalut), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /*Test du capteur biométrique*/
        BiometricManager biometricManager = BiometricManager.from(this);
        int canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if (canAuthenticate==BiometricManager.BIOMETRIC_SUCCESS){
            authenticate();
        }
        else if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            Toast.makeText(this, "Aucun capteur biométrique sur l'appareil", Toast.LENGTH_SHORT).show();
        } else if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            Toast.makeText(this, "Capteur temporairement indisponible", Toast.LENGTH_SHORT).show();
        } else if (canAuthenticate == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            Toast.makeText(this, "Aucune empreinte enregistrée", Toast.LENGTH_SHORT).show();
        }
        /*Récupération de la latitude et longitude stockées dans les préférences*/
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        float latitude = prefs.getFloat("latitude", 0f);
        float longitude = prefs.getFloat("longitude", 0f);


    }
    private void authenticate(){
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result){
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Empreinte reconnue !", Toast.LENGTH_SHORT).show();
                /*Là je compare la position actuelle avec la position enregistrée dans les prefs*/
                checkLocationAccess();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Erreur : " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Empreinte non reconnue", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authentification")
                .setSubtitle("Déverrouille avec ton empreinte")
                .setNegativeButtonText("Annuler")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }
    @SuppressLint("MissingPermission")
    private void checkLocationAccess() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        double savedLat = prefs.getFloat("latitude", 0);
        double savedLon = prefs.getFloat("longitude", 0);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(1000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location currentLocation = locationResult.getLastLocation();

                if (currentLocation != null) {
                    double currentLat = currentLocation.getLatitude();
                    double currentLon = currentLocation.getLongitude();

                    float[] results = new float[1];
                    Location.distanceBetween(savedLat, savedLon, currentLat, currentLon, results);

                    if (results[0] <= 50) {
                        startActivity(new Intent(MainActivity.this, GalleryActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Vous n'êtes pas à la bonne position", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Impossible d'obtenir votre position", Toast.LENGTH_SHORT).show();
                }

                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}


