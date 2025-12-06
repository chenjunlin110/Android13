package com.example.android;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.DataManager;
import com.example.android.model.UserData;
import com.example.android.model.Album;

public class MainActivity extends AppCompatActivity {
    private UserData userData;
    private RecyclerView albumList;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = DataManager.load(this);
        albumList = findViewById(R.id.album_list);
        albumList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlbumAdapter(userData.getAlbums(), new AlbumAdapter.Listener() {
            @Override
            public void onOpen(Album album) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.EXTRA_ALBUM_NAME, album.getName());
                startActivity(intent);
            }

            @Override
            public void onRename(Album album) {
                showRenameDialog(album);
            }

            @Override
            public void onDelete(Album album) {
                userData.removeAlbum(album.getName());
                adapter.notifyDataSetChanged();
            }
        });
        albumList.setAdapter(adapter);

        Button addAlbum = findViewById(R.id.btn_add_album);
        addAlbum.setOnClickListener(v -> {
            showAddDialog();
        });

        Button search = findViewById(R.id.btn_search);
        if (search != null) {
            search.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataManager.save(this, userData);
    }

    private void showAddDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Album")
                .setView(input)
                .setPositiveButton("Add", (d, which) -> {
                    String name = input.getText().toString().trim();
                    if (userData.addAlbum(name)) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Invalid or duplicate name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRenameDialog(Album album) {
        EditText input = new EditText(this);
        input.setText(album.getName());
        new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setView(input)
                .setPositiveButton("Save", (d, which) -> {
                    String newName = input.getText().toString().trim();
                    if (userData.renameAlbum(album.getName(), newName)) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Invalid or duplicate name", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
