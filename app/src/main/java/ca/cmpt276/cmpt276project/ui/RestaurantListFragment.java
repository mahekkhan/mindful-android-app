package ca.cmpt276.cmpt276project.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.ui.recyclerview.RestaurantAdapter;

/**
 * Handles the recyclerview for the restaurant list
 */
public class RestaurantListFragment extends Fragment {
    Context context;
    View view;
    RecyclerView recyclerView;

    public RestaurantListFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        recyclerView = view.findViewById(R.id.pager_recycler);
        return setupRestaurantList();
    }

    public void reloadList() {
        List<Restaurant> restaurants = RestaurantManager.getManagerInstance().getFilteredRestaurants();
        RestaurantAdapter adapter = new RestaurantAdapter(context, restaurants, getActivity(), true);
        recyclerView.setAdapter(adapter);
    }

    private View setupRestaurantList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<Restaurant> restaurants = RestaurantManager.getManagerInstance().getRestaurants();
        RestaurantAdapter adapter = new RestaurantAdapter(context, restaurants, getActivity(), true);
        recyclerView.setAdapter(adapter);
        return view;
    }

    //Necessary because if filters are set and the list hasn't been viewed at least once prior,
    //the list will not display the filtered results.
    @Override
    public void onStart() {
        super.onStart();
        reloadList();
    }
}