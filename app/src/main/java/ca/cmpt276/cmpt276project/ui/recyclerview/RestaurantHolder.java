package ca.cmpt276.cmpt276project.ui.recyclerview;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.Restaurant;

import static ca.cmpt276.cmpt276project.ui.DisplaySingleRestaurantActivity.FAVOURITE_DATA;

/**
 * ViewHolder for restaurant, needed for RecyclerView implementation
 */
class RestaurantHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView txtResName;
    private TextView txtResIssues;
    private TextView txtInspectionDate;
    private ImageView imgHazardIcon;
    private ImageView imgRestaurantIcon;
    private ImageView imgFavouriteIcon;
    private ImageView imgUpdateAlertIcon;

    RestaurantHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        txtResName = itemView.findViewById(R.id.res_card_text_title);
        txtResIssues = itemView.findViewById(R.id.res_card_text_issue_num);
        txtInspectionDate = itemView.findViewById(R.id.res_card_text_inspect_date);
        imgHazardIcon = itemView.findViewById(R.id.res_card_image_hazard);
        imgRestaurantIcon = itemView.findViewById(R.id.res_card_image_restaurant);
        imgFavouriteIcon = itemView.findViewById(R.id.favourites_res_list_img);
        imgUpdateAlertIcon = itemView.findViewById(R.id.fav_card_image_update);
    }

    void setDetails(Restaurant restaurant) {
        txtResName.setText(restaurant.getName());
        String totalIssues = context.getString(R.string.res_total_issue,
                restaurant.getSumTotalIssuesInLastInspection());
        txtResIssues.setText(totalIssues);
        String inspectionDate = context.getString(R.string.res_inspection_date,
                restaurant.getLatestInspectionDaysDifference());
        txtInspectionDate.setText(inspectionDate);
        setHazardIcon(restaurant.getLatestReportHazardLvl());
        setRestaurantIcon(restaurant);
        setFavouriteRestaurantIcon(restaurant);
    }

    private void setHazardIcon(String hazardLevel) {
        if (hazardLevel.equals("Moderate")) {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_yellow);
        } else if (hazardLevel.equals("High")) {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_red);
        } else {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_green);
        }
    }

    private void setRestaurantIcon(Restaurant restaurant) {
        if(restaurant.getName().contains("7-Eleven")) {
            imgRestaurantIcon.setImageResource(R.drawable.seven_eleven);

        }else if(restaurant.getName().contains("Panago")){
            imgRestaurantIcon.setImageResource(R.drawable.panago_logo);

        }else if(restaurant.getName().contains("Pizza Hut")) {
            imgRestaurantIcon.setImageResource(R.drawable.pizzahut);

        }else if(restaurant.getName().contains("Real Canadian Superstore")) {
            imgRestaurantIcon.setImageResource(R.drawable.superstore);

        }else if(restaurant.getName().contains("Safeway")) {
            imgRestaurantIcon.setImageResource(R.drawable.safeway);

        }else if(restaurant.getName().contains("Save On Foods")) {
            imgRestaurantIcon.setImageResource(R.drawable.saveonfoods);

        }else if(restaurant.getName().contains("A&W")) {
            imgRestaurantIcon.setImageResource(R.drawable.aandw);

        }else if(restaurant.getName().contains("Starbucks Coffee")) {
            imgRestaurantIcon.setImageResource(R.drawable.starbucks);

        }else if(restaurant.getName().contains("Blenz Coffee")) {
            imgRestaurantIcon.setImageResource(R.drawable.blenz);

        }else if(restaurant.getName().contains("Booster Juice")) {
            imgRestaurantIcon.setImageResource(R.drawable.boosterjuice);

        }else if(restaurant.getName().contains("Boston Pizza")) {
            imgRestaurantIcon.setImageResource(R.drawable.bostonpizza);

        }else if(restaurant.getName().contains("Subway")) {
            imgRestaurantIcon.setImageResource(R.drawable.subway);

        }else if(restaurant.getName().contains("Burger King")) {
            imgRestaurantIcon.setImageResource(R.drawable.burgerking);

        }else if(restaurant.getName().contains("Tim Horton's")) {
            imgRestaurantIcon.setImageResource(R.drawable.timhortons);

        }else if(restaurant.getName().contains("Domino's Pizza")) {
            imgRestaurantIcon.setImageResource(R.drawable.dominos);

        }else if(restaurant.getName().contains("Quizno's")) {
            imgRestaurantIcon.setImageResource(R.drawable.quiznos);

        }else {
            imgRestaurantIcon.setImageResource(R.drawable.restaurant_icon);
        }
    }

    private void setFavouriteRestaurantIcon(Restaurant restaurant) {
        SharedPreferences prefs = context.getSharedPreferences(FAVOURITE_DATA, Context.MODE_PRIVATE);
        String key = restaurant.getName() + FAVOURITE_DATA;
        boolean isFavourite = prefs.getBoolean(key, false);
        if (isFavourite) {
            imgFavouriteIcon.setImageResource(R.drawable.star_yellow);
        } else {
            imgFavouriteIcon.setImageResource(R.drawable.star_grey);
        }

        String key2 = restaurant.hasUpdate() + FAVOURITE_DATA;
        boolean hasUpdate = prefs.getBoolean(key2, false);
        if(hasUpdate){
            imgUpdateAlertIcon.setImageResource(R.drawable.exclamation_mark);
        } else {
            imgUpdateAlertIcon.setVisibility(View.INVISIBLE);
        }
    }
}