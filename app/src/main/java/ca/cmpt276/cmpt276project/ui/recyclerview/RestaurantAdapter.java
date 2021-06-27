package ca.cmpt276.cmpt276project.ui.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.ui.DisplaySingleRestaurantActivity;

import static ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity.MOVE_TO_RESTAURANT_REQUEST_CODE;

/**
 * Restaurant adapter, needed for RecyclerView implementation
 * Also handles OnClick events
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantHolder> {
    private Activity displayRestaurantActivity;
    private Context context;
    private List<Restaurant> restaurants;
    private RecyclerView recyclerView;
    private boolean isClickable;
    private final View.OnClickListener restaurantClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickEvent(v);
        }
    };

    private void clickEvent(View v) {
        if (isClickable) {
            int restaurantIndex = recyclerView.getChildAdapterPosition(v);

            Intent singleResIntent = DisplaySingleRestaurantActivity.makeIntent(context);
            singleResIntent.putExtra(Restaurant.RESTAURANT_KEY, restaurantIndex);
            displayRestaurantActivity.startActivityForResult(singleResIntent, MOVE_TO_RESTAURANT_REQUEST_CODE);
        }
    }

    public RestaurantAdapter(Context context, List<Restaurant> restaurants, Activity activity, boolean isClickable) {
        displayRestaurantActivity = activity;
        this.context = context;
        this.restaurants = restaurants;
        this.isClickable = isClickable;
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_card, parent,
                false);
        view.setOnClickListener(restaurantClickListener);
        return new RestaurantHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.setDetails(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}