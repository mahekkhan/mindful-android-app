package ca.cmpt276.cmpt276project.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.ui.recyclerview.InspectionAdapter;

import static ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity.MOVE_TO_RESTAURANT_REQUEST_CODE;

/**
 * Displays all of the inspections done on a restaurant along with some brief inspection details.
 * Also displays restaurant information
 */
public class DisplaySingleRestaurantActivity extends AppCompatActivity {
    public static final String FAVOURITE_DATA = "1337FAVOURITES";
    private SharedPreferences prefs;
    private int restaurantIndex;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences(FAVOURITE_DATA, MODE_PRIVATE);
        setContentView(R.layout.activity_display_single_restaurant);
        setupBackButton();
        loadIntentData();
        setTextViews();
        setupRecyclerView();
        setupCardClick();
        setFavouriteButtonClick();
    }

    private void setupCardClick() {
        MaterialCardView cardView = findViewById(R.id.singleRes_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restaurantIndexIntent = new Intent();
                restaurantIndexIntent.putExtra(Restaurant.RESTAURANT_KEY, restaurantIndex);
                setResult(MOVE_TO_RESTAURANT_REQUEST_CODE, restaurantIndexIntent);
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.singleRes_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Report> reports = restaurant.getReportsList();
        InspectionAdapter adapter = new InspectionAdapter(this, reports, restaurantIndex);
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadIntentData() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            restaurantIndex = data.getInt(Restaurant.RESTAURANT_KEY);
            restaurant = RestaurantManager.getManagerInstance().getFilteredRestaurants().get(restaurantIndex);
        }
    }

    private void setTextViews() {
        TextView textName = findViewById(R.id.singleRes_text_title);
        TextView textAddress = findViewById(R.id.singleRes_text_address);
        TextView textCoords = findViewById(R.id.singleRes_text_coords);

        textName.setText(restaurant.getName());
        textAddress.setText(restaurant.getLocation().getAddress());

        String coordinates = "" + restaurant.getLocation().getLatitude() + ", " +
                restaurant.getLocation().getLongitude();
        textCoords.setText(coordinates);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DisplaySingleRestaurantActivity.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setFavouriteButtonClick() {
        final ToggleButton favButton = findViewById(R.id.favToggleButton);
        favButton.setChecked(readState());

        favButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (favButton.isChecked()) {
                    // The toggle is enabled
                    saveFavouriteRestaurant(isChecked);
                    restaurant.setFavourite(true);
                    Toast.makeText(DisplaySingleRestaurantActivity.this, restaurant.getName()
                            + " is added to your favourites!", Toast.LENGTH_SHORT).show();

                } else if (!favButton.isChecked()){
                    // The toggle is disabled
                    saveFavouriteRestaurant(isChecked);
                    restaurant.setFavourite(false);
                    Toast.makeText(DisplaySingleRestaurantActivity.this, restaurant.getName()
                            + " is removed from your favourites!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void saveFavouriteRestaurant(boolean isFavourite) {
        final SharedPreferences.Editor editor = prefs.edit();
        String key = restaurant.getName() + FAVOURITE_DATA;
        editor.putBoolean(key, isFavourite);
        editor.apply();
    }

    private boolean readState() {
        String key = restaurant.getName() + FAVOURITE_DATA;
        return prefs.getBoolean(key, false);
    }
}
