package com.example.securedmemories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetupActivity extends AppCompatActivity {

    public EditText editAdresse;
    public Button buttonValider;

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
                    double lat = result.get(0).getLatitude();
                    double lon = result.get(0).getLongitude();
                    SharedPreferences prefs = getSharedPreferences("MyPrefs",MODE_PRIVATE);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putFloat("latitude",(float)lat);
                    editor.putFloat("longitude",(float)lon);
                    editor.apply();
                    Intent intent = new Intent(this,MainActivity.class);
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
    }
}