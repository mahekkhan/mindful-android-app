package ca.cmpt276.cmpt276project.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ca.cmpt276.cmpt276project.R;

import static ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity.TAG;

public class RestaurantSearchFragment extends Fragment {
    RestaurantViewPagerAdapter pagerAdapter;
    Context context;
    View view;

    public RestaurantSearchFragment(RestaurantViewPagerAdapter pagerAdapter, Context context) {
        this.pagerAdapter = pagerAdapter;
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_search, container, false);
        setupSearchButtons(view);
        return view;
    }

    private void setupSearchButtons(View view) {
        final EditText restaurantName = view.findViewById(R.id.editText_restaurantName);
        final EditText violationMax = view.findViewById(R.id.editText_maxViolation);
        final Spinner hazardLevelSpinner = view.findViewById(R.id.spinner_hazardLevel);
        final Switch favouriteSwitch = view.findViewById(R.id.favouritesSwitch);
        Button clearButton = view.findViewById(R.id.button_clearSearch);
        Button applyButton = view.findViewById(R.id.button_applySearch);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurantName.getText().clear();
                violationMax.getText().clear();
                hazardLevelSpinner.setSelection(0);
                favouriteSwitch.setChecked(false);
                SearchSettings search = SearchSettings.getSearchInstance();
                search.clear();
                pagerAdapter.reloadData();
                Toast.makeText(getContext(), "Cleared settings!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, search.toString());
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchSettings search = SearchSettings.getSearchInstance();
                search.setSearchSettings(restaurantName.getText().toString(),
                        hazardLevelSpinner.getSelectedItem().toString(),
                        violationMax.getText().toString(),
                        favouriteSwitch.isChecked());
                pagerAdapter.reloadData();
                Toast.makeText(getContext(), "Saved settings!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, search.toString());
            }
        });

        String[] hazardLevels = {"None", "Low", "Moderate", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, hazardLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardLevelSpinner.setAdapter(adapter);
    }
}
