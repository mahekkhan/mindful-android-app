package ca.cmpt276.cmpt276project.ui.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ca.cmpt276.cmpt276project.ui.RestaurantMapFragment;

/**
 * Used to edit the default clustering options for restaurant markers
 */
public class RestaurantMapClusterRenderer extends DefaultClusterRenderer {

    public RestaurantMapClusterRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {
        RestaurantClusterItem restaurantClusterItem = (RestaurantClusterItem) item;
        markerOptions.icon(restaurantClusterItem.getIcon());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return cluster.getSize() > 1 && RestaurantMapFragment.allowMarkerClustering;
    }
}
