package ca.cmpt276.cmpt276project.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ca.cmpt276.cmpt276project.R;

/**
 * Used to set the contents of a custom view used for the info window
 * of a marker.
 */
public class RestaurantInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View infoWindowView;

    public RestaurantInfoWindowAdapter(Context context) {
        infoWindowView =
                LayoutInflater.from(context).inflate(R.layout.restraurant_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView restaurantName = infoWindowView.findViewById(R.id.res_info_window_text_title);
        TextView restaurantAddress = infoWindowView.findViewById(R.id.res_info_window_text_address);
        TextView restaurantHazard = infoWindowView.findViewById(R.id.res_info_window_text_hazard);
        restaurantName.setText(marker.getTitle());

        if (marker.getSnippet() != null) {
            String[] snippet = marker.getSnippet().split("@");
            restaurantAddress.setText(snippet[0]);
            restaurantHazard.setText(snippet[1]);
        }

        return infoWindowView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return infoWindowView;
    }
}
