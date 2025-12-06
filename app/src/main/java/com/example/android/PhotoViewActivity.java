package com.example.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.model.Album;
import com.example.android.model.DataManager;
import com.example.android.model.Photo;
import com.example.android.model.Tag;
import com.example.android.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewActivity extends AppCompatActivity {
    public static final String EXTRA_ALBUM_NAME = "extra_album_name";
    public static final String EXTRA_PHOTO_INDEX = "extra_photo_index";

    private UserData userData;
    private Album album;
    private int index;

    private ImageView img;
    private TextView caption;
    private TextView tagsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        img = findViewById(R.id.img_full);
        caption = findViewById(R.id.tv_photo_caption);
        tagsView = findViewById(R.id.tv_tags);

        userData = DataManager.load(this);
        Intent intent = getIntent();
        String albumName = intent.getStringExtra(EXTRA_ALBUM_NAME);
        index = intent.getIntExtra(EXTRA_PHOTO_INDEX, 0);
        album = userData.getAlbum(albumName);
        if (album == null) {
            Toast.makeText(this, "Album not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (index < 0 || index >= album.getPhotoCount()) {
            index = 0;
        }

        Button prev = findViewById(R.id.btn_prev);
        Button next = findViewById(R.id.btn_next);
        Button addTag = findViewById(R.id.btn_add_tag);
        Button removeTag = findViewById(R.id.btn_remove_tag);
        Button movePhoto = findViewById(R.id.btn_move_photo);

        prev.setOnClickListener(v -> {
            if (index > 0) {
                index--;
                showPhoto();
            } else {
                Toast.makeText(this, "First photo", Toast.LENGTH_SHORT).show();
            }
        });
        next.setOnClickListener(v -> {
            if (index < album.getPhotoCount() - 1) {
                index++;
                showPhoto();
            } else {
                Toast.makeText(this, "Last photo", Toast.LENGTH_SHORT).show();
            }
        });
        addTag.setOnClickListener(v -> showAddTagDialog());
        removeTag.setOnClickListener(v -> showRemoveTagDialog());
        movePhoto.setOnClickListener(v -> showMoveDialog());

        showPhoto();
    }

    private void showPhoto() {
        if (album.getPhotoCount() == 0) {
            finish();
            return;
        }
        Photo p = album.getPhotos().get(index);
        img.setImageURI(Uri.parse(p.getUriString()));
        caption.setText(p.getFilename());
        StringBuilder sb = new StringBuilder("Tags: ");
        for (Tag t : p.getTags()) {
            sb.append(t.toString()).append("  ");
        }
        tagsView.setText(sb.toString());
    }

    private void showAddTagDialog() {
        String[] types = {"person", "location"};
        final Tag.TagType[] selected = {Tag.TagType.PERSON};
        EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Add Tag")
                .setSingleChoiceItems(types, 0, (d, which) -> {
                    selected[0] = which == 0 ? Tag.TagType.PERSON : Tag.TagType.LOCATION;
                })
                .setView(input)
                .setPositiveButton("Add", (d, w) -> {
                    String value = input.getText().toString().trim();
                    Photo p = album.getPhotos().get(index);
                    if (p.addTag(new Tag(selected[0], value))) {
                        DataManager.save(this, userData);
                        showPhoto();
                    } else {
                        Toast.makeText(this, "Duplicate tag", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRemoveTagDialog() {
        Photo p = album.getPhotos().get(index);
        List<Tag> tags = p.getTags();
        if (tags.isEmpty()) {
            Toast.makeText(this, "No tags", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> display = new ArrayList<>();
        for (Tag t : tags) {
            display.add(t.toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        new AlertDialog.Builder(this)
                .setTitle("Remove Tag")
                .setAdapter(adapter, (d, which) -> {
                    p.removeTag(tags.get(which));
                    DataManager.save(this, userData);
                    showPhoto();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMoveDialog() {
        List<Album> albums = userData.getAlbums();
        if (albums.size() <= 1) {
            Toast.makeText(this, "No other albums", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> names = new ArrayList<>();
        for (Album a : albums) {
            if (a != album) names.add(a.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        new AlertDialog.Builder(this)
                .setTitle("Move to")
                .setAdapter(adapter, (d, which) -> {
                    Album target = userData.getAlbum(names.get(which));
                    Photo p = album.getPhotos().get(index);
                    if (target.containsPhoto(p)) {
                        Toast.makeText(this, "Target already has this photo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    album.movePhotoTo(p, target);
                    DataManager.save(this, userData);
                    if (index >= album.getPhotoCount()) {
                        index = Math.max(0, album.getPhotoCount() - 1);
                    }
                    showPhoto();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
