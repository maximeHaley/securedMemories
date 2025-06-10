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
}