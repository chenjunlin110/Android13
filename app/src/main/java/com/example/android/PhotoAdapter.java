package com.example.android;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.Photo;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public interface Listener {
        void onView(Photo photo, int position);
        void onDelete(Photo photo, int position);
    }

    private final List<Photo> data;
    private final Listener listener;

    public PhotoAdapter(List<Photo> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        ImageButton delete;

        PhotoViewHolder(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.img_thumb);
            delete = itemView.findViewById(R.id.btn_delete_photo);
        }
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = data.get(position);
        holder.thumb.setImageURI(Uri.parse(photo.getUriString()));
        holder.thumb.setOnClickListener(v -> listener.onView(photo, holder.getAdapterPosition()));
        holder.delete.setOnClickListener(v -> listener.onDelete(photo, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
