package com.example.android;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.Photo;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.VH> {
    public interface Listener {
        void onView(ResultItem item);
    }

    public static class ResultItem {
        public final String albumName;
        public final int photoIndex;
        public final Photo photo;
        public ResultItem(String albumName, int photoIndex, Photo photo) {
            this.albumName = albumName;
            this.photoIndex = photoIndex;
            this.photo = photo;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView filename;
        TextView album;
        Button view;
        VH(View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.img_result_thumb);
            filename = itemView.findViewById(R.id.tv_result_filename);
            album = itemView.findViewById(R.id.tv_result_album);
            view = itemView.findViewById(R.id.btn_view_result);
        }
    }

    private final List<ResultItem> data;
    private final Listener listener;

    public SearchResultAdapter(List<ResultItem> data, Listener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ResultItem item = data.get(position);
        holder.thumb.setImageURI(Uri.parse(item.photo.getUriString()));
        holder.filename.setText(item.photo.getFilename());
        holder.album.setText("Album: " + item.albumName);
        holder.view.setOnClickListener(v -> listener.onView(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
