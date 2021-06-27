package ca.cmpt276.cmpt276project.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;

/**
 * Controls the swipe tab display for the restaurant map and list
 */
public class RestaurantViewPagerAdapter extends FragmentStateAdapter {
    private RestaurantManager restaurantManager;
    private Restaurant restaurant;
    private RestaurantMapFragment mapFragment;
    private RestaurantListFragment listFragment;
    private RestaurantSearchFragment searchFragment;
    private RestaurantFavouritesListFragment favouritesListFragment;
    private Context context;

    public RestaurantViewPagerAdapter(FragmentActivity fa, RestaurantManager restaurantManager,
                                      Context context) {
        super(fa);
        this.restaurantManager = restaurantManager;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            mapFragment = new RestaurantMapFragment();
            return mapFragment;
        } else if (position == 1) {
            listFragment = new RestaurantListFragment(context);
            return listFragment;
        } else if (position == 2) {
            searchFragment = new RestaurantSearchFragment(this, context);
            return searchFragment;
        } else {
            favouritesListFragment = new RestaurantFavouritesListFragment(context);
            return favouritesListFragment;
        }
    }

    public void reloadData() {
        if (listFragment != null) {
            listFragment.reloadList();
        }
        if (mapFragment != null) {
            mapFragment.reloadMap();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public RestaurantMapFragment getMapFragment() {
        return mapFragment;
    }

    public RestaurantListFragment getListFragment() {
        return listFragment;
    }

    public RestaurantSearchFragment getSearchFragment() {
        return searchFragment;
    }
}