package com.example.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    public interface Listener {
        void onOpen(Album album);
        void onRename(Album album);
        void onDelete(Album album);
    }

    private final List<Album> data;
    private final Listener listener;

    public AlbumAdapter(List<Album> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button open;
        Button rename;
        Button delete;

        AlbumViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_album_name);
            open = itemView.findViewById(R.id.btn_open);
            rename = itemView.findViewById(R.id.btn_rename);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = data.get(position);
        holder.name.setText(album.getName() + " (" + album.getPhotoCount() + ")");
        holder.open.setOnClickListener(v -> listener.onOpen(album));
        holder.rename.setOnClickListener(v -> listener.onRename(album));
        holder.delete.setOnClickListener(v -> listener.onDelete(album));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
