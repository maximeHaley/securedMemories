package com.example.securedmemories;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


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

        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Supprime toutes les préférences
            editor.apply(); // Sauvegarde la suppression

            Toast.makeText(this, "Préférences supprimées", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
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
        float latitude = prefs.getFloat("latitude", 0f); // 0f = valeur par défaut si rien trouvé
        float longitude = prefs.getFloat("longitude", 0f);
        TextView coordonneesText = findViewById(R.id.coordonnesText);
        coordonneesText.setText("Lat: " + latitude + "\nLon: " + longitude);

    }
    private void authenticate(){
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result){
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Empreinte reconnue !", Toast.LENGTH_SHORT).show();
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
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getLastLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this,location->{
            if(location!=null){
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                Toast.makeText(this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_LONG).show();
                /*Sauvegarde dans les préférences*/
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("latitude", (float) latitude);
                editor.putFloat("longitude", (float) longitude);
                editor.apply();
            }
            else {
                Toast.makeText(this, "Impossible de récupérer la localisation", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*ça sert à la demande de permission de localisation, même si j'ai pas compris toute la syntaxe*/
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}