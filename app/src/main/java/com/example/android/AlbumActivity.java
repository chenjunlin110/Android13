package com.example.android;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.Album;
import com.example.android.model.DataManager;
import com.example.android.model.Photo;
import com.example.android.model.UserData;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    public static final String EXTRA_ALBUM_NAME = "extra_album_name";

    private UserData userData;
    private Album album;
    private PhotoAdapter adapter;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                Uri uri = result.getData().getData();
                if (uri == null) return;
                // persist permission
                final int flags = result.getData().getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    getContentResolver().takePersistableUriPermission(uri, flags);
                } catch (Exception ignored) {}

                String filename = getDisplayName(uri);
                Photo photo = new Photo(uri.toString(), filename);
                album.addPhoto(photo);
                DataManager.save(this, userData);
                adapter.notifyDataSetChanged();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        userData = DataManager.load(this);
        String albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);
        album = userData.getAlbum(albumName);
        if (album == null) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView title = findViewById(R.id.tv_album_title);
        title.setText(album.getName());

        RecyclerView grid = findViewById(R.id.photo_grid);
        grid.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter(album.getPhotos(), new PhotoAdapter.Listener() {
            @Override
            public void onView(Photo photo, int position) {
                Intent intent = new Intent(AlbumActivity.this, PhotoViewActivity.class);
                intent.putExtra(PhotoViewActivity.EXTRA_ALBUM_NAME, album.getName());
                intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_INDEX, position);
                startActivity(intent);
            }

            @Override
            public void onDelete(Photo photo, int position) {
                album.removePhoto(photo);
                DataManager.save(AlbumActivity.this, userData);
                adapter.notifyDataSetChanged();
            }
        });
        grid.setAdapter(adapter);

        Button addPhoto = findViewById(R.id.btn_add_photo);
        addPhoto.setOnClickListener(v -> openPicker());
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private String getDisplayName(Uri uri) {
        String name = null;
        ContentResolver cr = getContentResolver();
        try (Cursor cursor = cr.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) {
                    name = cursor.getString(idx);
                }
            }
        } catch (Exception ignored) {}
        if (name == null) {
            String path = uri.getLastPathSegment();
            if (path != null) {
                int slash = path.lastIndexOf('/');
                name = slash >= 0 ? path.substring(slash + 1) : path;
            } else {
                name = "photo";
            }
        }
        return name;
    }
}
