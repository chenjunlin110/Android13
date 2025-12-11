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

        spinnerType1 = findViewById(R.id.spinner_type1);
        spinnerType2 = findViewById(R.id.spinner_type2);
        spinnerConnector = findViewById(R.id.spinner_connector);
        inputValue1 = findViewById(R.id.input_value1);
        inputValue2 = findViewById(R.id.input_value2);
        resultsView = findViewById(R.id.search_results);

        setupSpinners();

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

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data every time we return to this activity
        userData = DataManager.load(this);
        setupAutoComplete();
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

        // Update autocomplete when spinner selection changes
        spinnerType1.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateAutoComplete1));
        spinnerType2.setOnItemSelectedListener(new SimpleItemSelectedListener(this::updateAutoComplete2));
    }

    private void setupAutoComplete() {
        updateAutoComplete1();
        updateAutoComplete2();
        
        // Set threshold to 1 character for autocomplete to trigger
        inputValue1.setThreshold(1);
        inputValue2.setThreshold(1);
    }

    private void updateAutoComplete1() {
        if (userData == null) return;
        Tag.TagType type = getTypeFromSpinner(spinnerType1);
        List<String> values = userData.getTagValues(type);
        ArrayAdapter<String> acAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);
        inputValue1.setAdapter(acAdapter);
    }

    private void updateAutoComplete2() {
        if (userData == null) return;
        Tag.TagType type = getTypeFromSpinner(spinnerType2);
        List<String> values = userData.getTagValues(type);
        ArrayAdapter<String> acAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);
        inputValue2.setAdapter(acAdapter);
    }

    private boolean isPerson(Spinner spinner) {
        Object item = spinner.getSelectedItem();
        return item != null && "person".equalsIgnoreCase(item.toString());
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

        // Clear previous results
        results.clear();
        
        // Search across ALL albums and collect ALL matching photos
        for (Album album : userData.getAlbums()) {
            List<Photo> photos = album.getPhotos();
            for (int i = 0; i < photos.size(); i++) {
                Photo p = photos.get(i);
                
                // hasTag uses prefix matching (startsWith)
                boolean m1 = p.hasTag(t1, val1);
                boolean match;
                
                if (val2.isEmpty()) {
                    // Single tag search
                    match = m1;
                } else {
                    // Two tag search with AND/OR
                    boolean m2 = p.hasTag(t2, val2);
                    match = "AND".equals(connector) ? (m1 && m2) : (m1 || m2);
                }
                
                if (match) {
                    // Add every matching photo from every album
                    results.add(new SearchResultAdapter.ResultItem(album.getName(), i, p));
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Found " + results.size() + " photo(s)", Toast.LENGTH_SHORT).show();
    }
}