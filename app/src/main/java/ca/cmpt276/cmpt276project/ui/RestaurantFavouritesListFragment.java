package ca.cmpt276.cmpt276project.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.ui.recyclerview.RestaurantAdapter;

public class RestaurantFavouritesListFragment extends Fragment {
    Context context;
    View view;
    RecyclerView recyclerView;

    public RestaurantFavouritesListFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restaurant_favourites_list, container, false);
        recyclerView = view.findViewById(R.id.fav_recycler);
        return setupFavouritesList();
    }

    public void reloadFavouritesList() {
        List<Restaurant> restaurants = RestaurantManager.getManagerInstance().getRestaurants();
        List<Restaurant> favourites = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.isFavourite()) {
                favourites.add(restaurant);
            }
        }
        RestaurantAdapter adapter = new RestaurantAdapter(context, favourites, getActivity(), false);
        recyclerView.setAdapter(adapter);
    }

    private View setupFavouritesList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        reloadFavouritesList();
        return view;
    }

    //Necessary because if filters are set and the list hasn't been viewed at least once prior,
    //the list will not display the filtered results.
    @Override
    public void onStart() {
        super.onStart();
        reloadFavouritesList();
    }
}
