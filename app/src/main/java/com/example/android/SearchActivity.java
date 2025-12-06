package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.model.Album;
import com.example.android.model.DataManager;
import com.example.android.model.Photo;
import com.example.android.model.Tag;
import com.example.android.model.UserData;
import com.example.android.SimpleItemSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private UserData userData;
    private Spinner spinnerType1;
    private Spinner spinnerType2;
    private Spinner spinnerConnector;
    private AutoCompleteTextView inputValue1;
    private AutoCompleteTextView inputValue2;
    private RecyclerView resultsView;
    private final List<SearchResultAdapter.ResultItem> results = new ArrayList<>();
    private SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userData = DataManager.load(this);

        spinnerType1 = findViewById(R.id.spinner_type1);
        spinnerType2 = findViewById(R.id.spinner_type2);
        spinnerConnector = findViewById(R.id.spinner_connector);
        inputValue1 = findViewById(R.id.input_value1);
        inputValue2 = findViewById(R.id.input_value2);
        resultsView = findViewById(R.id.search_results);

        setupSpinners();
        setupAutoComplete();

        adapter = new SearchResultAdapter(results, item -> {
            Intent intent = new Intent(SearchActivity.this, PhotoViewActivity.class);
            intent.putExtra(PhotoViewActivity.EXTRA_ALBUM_NAME, item.albumName);
            intent.putExtra(PhotoViewActivity.EXTRA_PHOTO_INDEX, item.photoIndex);
            startActivity(intent);
        });
        resultsView.setLayoutManager(new LinearLayoutManager(this));
        resultsView.setAdapter(adapter);

        Button run = findViewById(R.id.btn_search_run);
        run.setOnClickListener(v -> runSearch());
    }

    private void setupSpinners() {
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"person", "location"});
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType1.setAdapter(typeAdapter);
        spinnerType2.setAdapter(typeAdapter);

        ArrayAdapter<String> connectorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"OR", "AND"});
        connectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConnector.setAdapter(connectorAdapter);
    }

    private void setupAutoComplete() {
        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                userData.getTagValues(Tag.TagType.PERSON));
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                userData.getTagValues(Tag.TagType.LOCATION));
        inputValue1.setAdapter(locationAdapter);
        inputValue2.setAdapter(locationAdapter);

        spinnerType1.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> {
            inputValue1.setAdapter(isPerson(spinnerType1) ? personAdapter : locationAdapter);
        }));
        spinnerType2.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> {
            inputValue2.setAdapter(isPerson(spinnerType2) ? personAdapter : locationAdapter);
        }));
    }

    private boolean isPerson(Spinner spinner) {
        return "person".equalsIgnoreCase(spinner.getSelectedItem().toString());
    }

    private Tag.TagType getTypeFromSpinner(Spinner spinner) {
        return isPerson(spinner) ? Tag.TagType.PERSON : Tag.TagType.LOCATION;
    }

    private void runSearch() {
        String val1 = inputValue1.getText().toString().trim();
        if (val1.isEmpty()) {
            Toast.makeText(this, "Value 1 required", Toast.LENGTH_SHORT).show();
            return;
        }
        Tag.TagType t1 = getTypeFromSpinner(spinnerType1);
        Tag.TagType t2 = getTypeFromSpinner(spinnerType2);
        String connector = spinnerConnector.getSelectedItem().toString().toUpperCase(Locale.ROOT);
        String val2 = inputValue2.getText().toString().trim();

        results.clear();
        // manual search to retain album info
        for (Album album : userData.getAlbums()) {
            List<Photo> photos = album.getPhotos();
            for (int i = 0; i < photos.size(); i++) {
                Photo p = photos.get(i);
                boolean m1 = p.hasTag(t1, val1);
                boolean match;
                if (val2.isEmpty()) {
                    match = m1;
                } else {
                    boolean m2 = p.hasTag(t2, val2);
                    match = "AND".equals(connector) ? (m1 && m2) : (m1 || m2);
                }
                if (match) {
                    results.add(new SearchResultAdapter.ResultItem(album.getName(), i, p));
                }
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Found " + results.size() + " photos", Toast.LENGTH_SHORT).show();
    }
}
