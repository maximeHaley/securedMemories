package com.example.securedmemories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> imageUris = new ArrayList<>();
    private ImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // grille de 3 colonnes

        List<Uri> images = new ArrayList<>();

        Set<String> savedUriStrings = getSharedPreferences("gallery_prefs", MODE_PRIVATE)
                .getStringSet("images", new HashSet<>());

        for (String uriStr : savedUriStrings) {
            imageUris.add(Uri.parse(uriStr));
        }

        adapter = new ImageAdapter(this, images);
        recyclerView.setAdapter(adapter);

        Button buttonAddImage = findViewById(R.id.buttonAddImage);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(adapter);

        buttonAddImage.setOnClickListener(v -> openImagePicker());
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

    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    // Nom du fichier privé
                    String fileName = "img_" + System.currentTimeMillis() + ".jpg";
                    File destFile = new File(getFilesDir(), fileName);

                    // Copier le contenu dans le fichier privé
                    try (InputStream in = getContentResolver().openInputStream(sourceUri);
                         OutputStream out = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[4096];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }

                    // Ajouter au RecyclerView (via URI interne)
                    imageUris.add(Uri.fromFile(destFile));
                    adapter.notifyItemInserted(imageUris.size() - 1);
                    // Enregistrer dans les préférences
                    getSharedPreferences("gallery_prefs", MODE_PRIVATE)
                            .edit()
                            .putStringSet("images", new HashSet<>(toStringList(imageUris)))
                            .apply();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Erreur lors de la copie de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private List<String> toStringList(List<Uri> uris) {
        List<String> list = new ArrayList<>();
        for (Uri uri : uris) {
            list.add(uri.toString());
        }
        return list;
    }



}