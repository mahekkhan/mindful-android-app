package ca.cmpt276.cmpt276project.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.ui.DisplaySingleRestaurantActivity;
import ca.cmpt276.cmpt276project.ui.map.RestaurantClusterItem;
import ca.cmpt276.cmpt276project.ui.map.RestaurantInfoWindowAdapter;
import ca.cmpt276.cmpt276project.ui.map.RestaurantMapClusterRenderer;

import static android.content.Context.LOCATION_SERVICE;
import static ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity.TAG;

/**
 * Handles all map related things from configuration to displaying the map itself
 */
//https://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
public class RestaurantMapFragment extends Fragment {
    public static boolean allowMarkerClustering = true;
    private MapView mMapView;
    private GoogleMap googleMap;
    private LocationManager mLocationManager;
    private Location myLocation;
    private ClusterManager<RestaurantClusterItem> mClusterManager;
    private List<RestaurantClusterItem> restaurantClusterItemList = new ArrayList<>();
    private RestaurantMapClusterRenderer restaurantMapClusterRenderer;
    private int idleTimerSeconds;
    private boolean isIdleTimerActive = true;
    final Handler handler = new Handler();

    public RestaurantMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(Objects.requireNonNull(getActivity()).getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                onMapReadyCall(mMap);
            }
        });

        return rootView;
    }

    private void onMapReadyCall(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setInfoWindowAdapter(new RestaurantInfoWindowAdapter(getContext()));
        Objects.requireNonNull(getActivity()).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    private void setupCameraReturnWhenIdle() {
        updateIdleTimer();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                resetIdleTimer();
            }
        });
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                resetIdleTimer();
                Log.e(TAG, "ZOOM: " + googleMap.getCameraPosition().zoom);
                updateAllowMarketClustering();
            }
        });
    }

    private void updateAllowMarketClustering() {
        if (googleMap.getCameraPosition().zoom > 16) {
            allowMarkerClustering = false;
        } else {
            allowMarkerClustering = true;
        }
    }

    private void resetIdleTimer() {
        idleTimerSeconds = 0;
        isIdleTimerActive = true;
    }

    private void updateIdleTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isIdleTimerActive) {
                    idleTimerSeconds++;
                }
                updateIdleTimer();
            }
        }, 1000);

        if (idleTimerSeconds == 10) {
            idleTimerSeconds = 0;
            moveCameraToCurrentLocation(true);
        }
    }

    public void selectRestaurant(final int restaurantIndex) {
        List<Restaurant> restaurants = RestaurantManager.getManagerInstance().getFilteredRestaurants();
        Restaurant restaurant = restaurants.get(restaurantIndex);

        ca.cmpt276.cmpt276project.model.Location restaurantLocation = restaurant.getLocation();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(restaurantLocation.getLatitude(), restaurantLocation.getLongitude()), 12));
        updateAllowMarketClustering();
        loadSelectInfoWindow(restaurantIndex);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(restaurantLocation.getLatitude(), restaurantLocation.getLongitude()), 20));
    }

    private void loadSelectInfoWindow(final int restaurantIndex) {
        mClusterManager.cluster();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Marker restaurantMarker =
                        restaurantMapClusterRenderer.getMarker(restaurantClusterItemList.get(restaurantIndex));
                if (restaurantMarker != null) {
                    restaurantMarker.showInfoWindow();
                } else {
                    loadSelectInfoWindow(restaurantIndex);
                }
            }
        }, 1000);
        isIdleTimerActive = false;
    }

    public void reloadMap() {
        if (restaurantClusterItemList != null) {
            restaurantClusterItemList.clear();
        }
        if (mClusterManager != null) {
            mClusterManager.clearItems();
            restaurantMapClusterRenderer =
                    new RestaurantMapClusterRenderer(getActivity(), googleMap, mClusterManager);
            mClusterManager.setRenderer(restaurantMapClusterRenderer);
        }
        addRestaurantMarkers();
    }

    private void addRestaurantMarkers() {
        List<Restaurant> restaurants = RestaurantManager.getManagerInstance().getFilteredRestaurants();

        //'i' is used to keep track of restaurant index, so that the marker has the data on where to
        //find the correct restaurant from the restaurant list when needed
        int i = 0;
        int offsetNumber = 0;
        boolean sameLocation = false;
        List<ca.cmpt276.cmpt276project.model.Location> locations = new ArrayList<>();
        for (Restaurant restaurant: restaurants) {
            ca.cmpt276.cmpt276project.model.Location restaurantLocation = restaurant.getLocation();

            for (ca.cmpt276.cmpt276project.model.Location location : locations) {
                double latitudeDifference =
                        Math.abs(restaurantLocation.getLatitude() - location.getLatitude());

                double longitudeDifference =
                        Math.abs(restaurantLocation.getLongitude() - location.getLongitude());
                if (latitudeDifference == 0 && longitudeDifference == 0) {
                    offsetNumber++;
                    sameLocation = true;
                } else if (latitudeDifference > 0.01 || longitudeDifference > 0.01) {
                    offsetNumber = 0;
                }
            }

            LatLng restaurantCoordinates =
                    new LatLng(restaurantLocation.getLatitude(), restaurantLocation.getLongitude());

            if (sameLocation) {
                restaurantCoordinates =
                        new LatLng(restaurantLocation.getLatitude() + 0.00002 * offsetNumber,
                                restaurantLocation.getLongitude() + 0.00002 * offsetNumber);
            }

            BitmapDescriptor icon = setMarkerIcon(restaurant);

            RestaurantClusterItem restaurantItem = new RestaurantClusterItem(
                    restaurantCoordinates, restaurant.getName(), restaurant.getLocation().getAddress()
                    + "@Hazard Level: " + restaurant.getLatestReportHazardLvl(), i,
                    icon
            );

            restaurantClusterItemList.add(restaurantItem);
            mClusterManager.addItem(restaurantItem);
            locations.add(restaurantLocation);
            i++;
            sameLocation = false;
        }
    }

    private BitmapDescriptor setMarkerIcon(Restaurant restaurant) {
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        if (restaurant.getLatestReportHazardLvl().equals("Moderate")) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if (restaurant.getLatestReportHazardLvl().equals("High")) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        return icon;
    }

    //https://stackoverflow.com/a/21964693
    //Used to set custom info windows for cluster items
    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(Objects.requireNonNull(getContext()), googleMap);
        restaurantMapClusterRenderer =
                new RestaurantMapClusterRenderer(getActivity(), googleMap, mClusterManager);
        mClusterManager.setRenderer(restaurantMapClusterRenderer);

        googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mClusterManager.getMarkerCollection().setInfoWindowAdapter(
                new RestaurantInfoWindowAdapter(getContext()));

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<RestaurantClusterItem>() {
            @Override
            public void onClusterItemInfoWindowClick(RestaurantClusterItem restaurantClusterItem) {
                Intent singleResIntent = DisplaySingleRestaurantActivity.makeIntent(getContext());
                singleResIntent.putExtra(
                        Restaurant.RESTAURANT_KEY, restaurantClusterItem.getIndexInRestaurantList());
                Objects.requireNonNull(getContext()).startActivity(singleResIntent);
            }
        });

        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);

    }


    public void enableLocationFunctions() {
        googleMap.setMyLocationEnabled(true);
        mLocationManager =
                (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        moveCameraToCurrentLocation(false);
        setupCameraReturnWhenIdle();
        setUpClusterer();
        addRestaurantMarkers();
    }

    private void moveCameraToCurrentLocation(final boolean animateToLocation) {
        myLocation = getLastKnownLocation();
        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);

        if (animateToLocation) {
            googleMap.animateCamera(cameraUpdate);
        } else {
            googleMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}