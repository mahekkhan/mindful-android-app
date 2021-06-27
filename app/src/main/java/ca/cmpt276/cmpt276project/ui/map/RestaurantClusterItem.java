package ca.cmpt276.cmpt276project.ui.map;

import android.graphics.drawable.Icon;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Restaurant Marker that is compatible with clustering
 */
public class RestaurantClusterItem implements ClusterItem {
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private int indexInRestaurantList;
    private BitmapDescriptor mIcon;

    public RestaurantClusterItem(LatLng latlng) {
        mPosition = latlng;
    }

    public RestaurantClusterItem(LatLng latLng, String title, String snippet, int index,
                                 BitmapDescriptor icon) {
        mPosition = latLng;
        mTitle = title;
        mSnippet = snippet;
        indexInRestaurantList = index;
        mIcon = icon;
    }

    public int getIndexInRestaurantList() {
        return indexInRestaurantList;
    }

    public BitmapDescriptor getIcon() {
        return mIcon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
