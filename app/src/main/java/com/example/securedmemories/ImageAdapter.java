package com.example.securedmemories;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bumptech.glide.Glide;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Uri> imageUris;
    private Context context;

    public ImageAdapter(Context context, List<Uri> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(context).load(imageUri).into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("imageUri", imageUri.toString());
            context.startActivity(intent);
        });
        holder.imageView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer l'image")
                    .setMessage("Voulez-vous supprimer cette image ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        /* Supprimer le fichier si existant*/
                        File file = new File(imageUri.getPath());
                        if (file.exists()) {
                            boolean deleted = file.delete();
                            if (!deleted) {
                                Toast.makeText(context, "Échec de la suppression du fichier", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        imageUris.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, imageUris.size());
                        SharedPreferences prefs = context.getSharedPreferences("gallery_prefs", Context.MODE_PRIVATE);
                        Set<String> imageSet = new HashSet<>(prefs.getStringSet("images", new HashSet<>()));
                        imageSet.remove(imageUri.toString());
                        prefs.edit().putStringSet("images", imageSet).apply();

                        Toast.makeText(context, "Image supprimée", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Non", null)
                    .show();

            return true;
        });
    }



    @Override
    public int getItemCount() {
        return imageUris.size();
    }
    private void saveUrisToPrefs() {
        SharedPreferences prefs = context.getSharedPreferences("gallery_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> uriStrings = new HashSet<>();
        for (Uri uri : imageUris) {
            uriStrings.add(uri.toString());
        }

        editor.putStringSet("imageUris", uriStrings);
        editor.apply();
    }

}
