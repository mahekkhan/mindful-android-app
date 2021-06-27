package ca.cmpt276.cmpt276project.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.R;

import static ca.cmpt276.cmpt276project.ui.DisplaySingleRestaurantActivity.FAVOURITE_DATA;
import static java.nio.charset.Charset.forName;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Displays the list of restaurants along with a brief detailing of their issues and hazard level
 */
//https://gist.github.com/mitchtabian/2b9a3dffbfdc565b81f8d26b25d059bf
public class DisplayRestaurantActivity extends AppCompatActivity implements Response.Listener<JSONObject>, ResponseListener{

    private SharedPreferences prefs;
    public static final String FAVOURITE_DATA = "1337FAVOURITES";

    public static final String TAG = "Captain's Log";
    public static final int MOVE_TO_RESTAURANT_REQUEST_CODE = 1337;
    private RestaurantManager restaurantManager;
    private RestaurantViewPagerAdapter pagerAdapter;
    private ViewPager2 viewPager;

    private final String NAME_TIME = "lastUpdateTimes";
    private final String TAG_RESTAURANT = "lastRestaurantTime";
    private final String TAG_INSPECTION = "lastInspectionTime";
    private String restaurantFileNameString = "restaurants.csv";
    private String inspectionFileNameString = "inspection_list.csv";

    public Context context;

    private DisplayRestaurantActivity currentInstance = this;

    private String lastRestaurantUpdateTime = "";
    private String lastInspectionUpdateTime;
    private String dataType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getApplicationContext();
        restaurantManager = RestaurantManager.getManagerInstance();

        readAllViolations();

        final Long lastUpdateTime = getFromSharedPref(NAME_TIME, TAG_INSPECTION);

        Log.e("testing.........saved time...........", lastUpdateTime + "");
        if (lastUpdateTime == 0) {
            copyRawDataToInternalStorage();
        }

//        DownloadThread thread = new DownloadThread();
//        thread.execute();

//        ReadThread readThread = new ReadThread();
//        readThread.execute();
        updateData();

        // Read files from internal storage
        String destinationRestaurant = this.context.getFilesDir().getPath() + File.separator + restaurantFileNameString;
        String destinationReport = this.context.getFilesDir().getPath() + File.separator + inspectionFileNameString;
//        readRestaurantAndReportFiles(destinationRestaurant, destinationReport);

        setContentView(R.layout.activity_display_restaurant);
        setupViewPager();
        setupTabs();
        loadFavourites();
    }

    public class DownloadThread extends AsyncTask<String, Integer, Integer> {

//        private boolean answerUpdate;

        @Override
        protected void onPreExecute() {
//            updateData();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            updateData();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {

        }
    }

    public class ReadThread extends AsyncTask<String, Integer, Integer> {

//        private boolean answerUpdate;

        @Override
        protected void onPreExecute() {
//            updateData();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                get(3, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
//            String destinationRestaurant = getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
//            String destinationReport = getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
//            readRestaurantAndReportFiles(destinationRestaurant, destinationReport);
            setContentView(R.layout.activity_display_restaurant);
            loadFavourites();
            setupViewPager();
            setupTabs();
        }
    }


    protected void updateData(){

        final String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
        final String inspectionURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final RequestQueue requestQueueForRestaurantTime = Volley.newRequestQueue(this);


        final Long lastUpdateTime = getFromSharedPref(NAME_TIME, TAG_INSPECTION);

        Log.e("testing.........saved time...........", lastUpdateTime + "");
//        if (lastUpdateTime == 0) {
//            copyRawDataToInternalStorage();
//        }

        // CHECK TIME HERE
        long timeDifference;
        final Calendar today = Calendar.getInstance();
        timeDifference = (today.getTimeInMillis()- lastUpdateTime) / 3600000;
        timeDifference = Math.abs(timeDifference);
        // get inspection date

        Log.e("testing.........today in milis...........", today.getTimeInMillis() + "");
        Log.e("testing.........before...........", timeDifference + "");
        if (timeDifference > 20) {
            Log.e("testing.........inside time diff...........", timeDifference + "");
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");

            JsonObjectRequest objectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    restaurantURL,
                    null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject result = response.getJSONObject("result");
                                JSONArray resourceList = result.getJSONArray("resources");
                                // get format in CSV
                                int index = -1;
                                for (int i = 0; i < resourceList.length(); i++) {
                                    String formatName = resourceList.getJSONObject(i).getString("format");
                                    if (formatName.toLowerCase().equals("csv")) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index < 0) {
                                    return;
                                }

                                JSONObject resources = resourceList.getJSONObject(index);
                                String lastModificationTime = resources.getString("last_modified");
                                Log.e("getRestaurantDate----------in try-------", lastModificationTime);
                                DisplayRestaurantActivity.this.lastRestaurantUpdateTime = lastModificationTime;

                                try {
                                    final Date lastModifiedRestaurantTime = sdf.parse(lastModificationTime);
                                    Log.e("testing.........in try.....last rest......", lastModifiedRestaurantTime.getTime() + " " + DisplayRestaurantActivity.this.lastRestaurantUpdateTime);

                                    JsonObjectRequest objectRequest = new JsonObjectRequest(
                                            Request.Method.GET,
                                            inspectionURL,
                                            null,
                                            new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        JSONObject result = response.getJSONObject("result");
                                                        JSONArray resourceList = result.getJSONArray("resources");
                                                        // get format in CSV
                                                        int index = -1;
                                                        for (int i = 0; i < resourceList.length(); i++) {
                                                            String formatName = resourceList.getJSONObject(i).getString("format");
                                                            if (formatName.toLowerCase().equals("csv")) {
                                                                index = i;
                                                                break;
                                                            }
                                                        }
                                                        if (index < 0) {
                                                            return;
                                                        }

                                                        JSONObject resources = resourceList.getJSONObject(index);
                                                        String lastModificationTime = resources.getString("last_modified");
                                                        Log.e("getRestaurantDate----------in try-------", lastModificationTime);

                                                        try {
                                                            final Date lastModifiedInspectionTime = sdf.parse(lastModificationTime);
                                                            Log.e("testing.........in try.....last insp......", lastModifiedInspectionTime.getTime() + " " + lastModificationTime);

                                                            Long restaurantTimeInMilis = lastModifiedRestaurantTime.getTime();
                                                            Long inspectionTimeInMilis = lastModifiedInspectionTime.getTime();

                                                            if (lastUpdateTime < restaurantTimeInMilis ||
                                                                    lastUpdateTime < inspectionTimeInMilis) {
                                                                DisplayUpdateAlert dialog = new DisplayUpdateAlert(getApplicationContext());
                                                                dialog.show(getSupportFragmentManager(), "Dialogue alert");
                                                            } else {
                                                                String destinationRestaurant = getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
                                                                String destinationReport = getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
                                                                readRestaurantAndReportFiles(destinationRestaurant, destinationReport);
                                                            }
                                                        } catch (ParseException e) {
                                                            Log.e("testing.........in try......last rest.....", "catching parse exc");
                                                        }
                                                    } catch (JSONException e) {
                                                        //e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.e("getInspectionData Error", error.toString());
                                                }
                                            }
                                    );

                                    requestQueue.add(objectRequest);

                                } catch (ParseException e) {
                                    Log.e("testing.........in try......last insp.....", "catching parse exc");
                                }
                            } catch (JSONException e) {
                                //e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("getRestaurantData Error", error.toString());
                        }
                    }
            );

            requestQueue.add(objectRequest);
        } else {
            String destinationRestaurant = getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
            String destinationReport = getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
            readRestaurantAndReportFiles(destinationRestaurant, destinationReport);
        }

    }

    @Override
    public void receiveResponse(String toSave, String tag) {
        if (tag.equals(DisplayRestaurantActivity.this.TAG_RESTAURANT)) {
            this.lastRestaurantUpdateTime = toSave;
        } else if (tag.equals(DisplayRestaurantActivity.this.TAG_INSPECTION)) {
            this.lastInspectionUpdateTime = toSave;
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject result = response.getJSONObject("result");
            JSONArray resourceList = result.getJSONArray("resources");
            // get format in CSV
            int index = -1;
            for (int i = 0; i < resourceList.length(); i++) {
                String formatName = resourceList.getJSONObject(i).getString("format");
                if (formatName.toLowerCase().equals("csv")) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                return;
            }

            JSONObject resources = resourceList.getJSONObject(index);
            String lastModificationTime = resources.getString("last_modified");
            Log.e("getRestaurantDate----------in try-------", lastModificationTime);


            if (dataType.equals(DisplayRestaurantActivity.this.TAG_RESTAURANT)) {
                this.lastRestaurantUpdateTime = lastModificationTime;
            } else if (dataType.equals(DisplayRestaurantActivity.this.TAG_INSPECTION)) {
                this.lastInspectionUpdateTime = lastModificationTime;
            }

        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    protected void getRestaurantFromWeb(String url, RequestQueue requestQueue){
        // RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rawData = response;

                        // Extract data as string to restaurant manager class
                        // restaurantManager.getRestaurantDataFromWeb(rawData);

                        // store data to internal storage
                        Context context = getApplicationContext();
                        String destinationFile = context.getFilesDir().getPath() + File.separator + restaurantFileNameString;
                        try {
                            CopyStringToInternalStorage(rawData, destinationFile);
                        } catch (Exception e) {
//                      e.printStackTrace();
                        }
                        // store data in sharedPreferences ( see function )
                        // storeRestaurantList(rawData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getRestaurantFromWeb Error", error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    public void getInspectionFromWeb(String url, RequestQueue requestQueue) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rawData = response;

                        // Extract data as string to restaurant manager class
                        // restaurantManager.getInspectionDataFromWeb(rawData);

                        // store data to internal storage
                        Context context = getApplicationContext();
                        String destinationFile = context.getFilesDir().getPath() + File.separator + inspectionFileNameString;
                        try {
                            CopyStringToInternalStorage(rawData, destinationFile);

                        } catch (Exception e) {
//                      e.printStackTrace();
                        }
                        // store data in sharedPreferences ( see function )
//                        storeRestaurantList(rawData);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getInspectionFromWeb Error", error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    protected void getInspectionRawDataURL(String url, final RequestQueue requestQueue){
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            JSONArray resourceList = result.getJSONArray("resources");
                            // get format in CSV
                            int index = -1;
                            for (int i = 0; i < resourceList.length(); i++) {
                                String formatName = resourceList.getJSONObject(i).getString("format");
                                if (formatName.toLowerCase().equals("csv")) {
                                    index = i;
                                    break;
                                }
                            }
                            JSONObject resources = resourceList.getJSONObject(index);
                            String inspectionURL = resources.getString("url");

                            getInspectionFromWeb(inspectionURL, requestQueue);

                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getInspectionRawData Error", error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    private Long getFromSharedPref(String name, String key){
        SharedPreferences prefs = getSharedPreferences(name, MODE_PRIVATE);
        Long time = prefs.getLong(key, 0);

        return time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MOVE_TO_RESTAURANT_REQUEST_CODE) {
            viewPager.setCurrentItem(0);
            int restaurantIndex = data.getIntExtra(Restaurant.RESTAURANT_KEY, 0);
            pagerAdapter.getMapFragment().selectRestaurant(restaurantIndex);
        }
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager,
            new TabLayoutMediator.TabConfigurationStrategy() {
                String[] tabName = {"Restaurant Map", "Restaurant List", "Search", "Favourites"};

                @Override
                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                    tab.setText(tabName[position]);
                    if (position == 0) {
                        tab.setIcon(R.drawable.map_icon);
                    } else if (position == 1) {
                        tab.setIcon(R.drawable.list_icon);
                    } else if (position == 2) {
                        tab.setIcon(R.drawable.search_icon);
                    } else {
                        tab.setIcon(R.drawable.star_icon);
                    }
                }
            }
        ).attach();
    }

    private void loadFavourites() {
        SharedPreferences appData = this.getSharedPreferences(FAVOURITE_DATA, MODE_PRIVATE);
        for (Restaurant restaurant : restaurantManager.getRestaurants()) {
            String key = restaurant.getName() + FAVOURITE_DATA;
            if (appData.getBoolean(key, false)) {
                restaurant.setFavourite(true);
            }
        }
    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager2);
        pagerAdapter = new RestaurantViewPagerAdapter(this, restaurantManager,
                DisplayRestaurantActivity.this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);
    }

    // Copies files to internal storage
    private void copyRawDataToInternalStorage() {
        restaurantManager = RestaurantManager.getManagerInstance();

        if (!restaurantManager.getIsDataLoaded()) {
            // copy from raw to internal storage
            String destinationRestaurant = context.getFilesDir().getPath() + File.separator + restaurantFileNameString;
            String destinationReport = context.getFilesDir().getPath() + File.separator + inspectionFileNameString;
            try {
                CopyRawToInternalStorage(R.raw.restaurants, destinationRestaurant);
                CopyRawToInternalStorage(R.raw.inspection_list, destinationReport);
            } catch (Exception e) {
//                e.printStackTrace();
            }
            restaurantManager.setIsDataLoaded(true);
        }
    }

    private void CopyStringToInternalStorage( String rawData, String destinationFile) throws IOException {
        InputStream inputStream =  new ByteArrayInputStream(rawData.getBytes());
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        byte[] buffer = rawData.getBytes();
        int length = inputStream.read(buffer);
        while(length > 0){
            outputStream.write(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void CopyRawToInternalStorage(int idOfRawFile, String destinationPath) throws IOException {
        InputStream inputStream = getResources().openRawResource(idOfRawFile);
        FileOutputStream outputStream = new FileOutputStream(destinationPath);
        byte[] buffer = new byte[inputStream.available()];
        int length = inputStream.read(buffer);
        while (length > 0) {
            outputStream.write(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void readAllViolations() {
        InputStream inputStream = getResources().openRawResource(R.raw.all_violations);
        restaurantManager.readViolationList(new BufferedReader(new InputStreamReader(inputStream, forName("UTF-8"))));
    }

    private void readRestaurantAndReportFiles(String destinationRestaurant, String destinationReport) {
        File restaurantFile = new File(destinationRestaurant);
        File reportsFile = new File(destinationReport);
        try {
            Log.e("reading file in main()", "..............succeeded");
            restaurantManager.readRestaurantDetail(new BufferedReader(new FileReader(restaurantFile)));
            restaurantManager.readReports(new BufferedReader(new FileReader(reportsFile)));
            Log.e("reading file in main()", "..............succeeded");
        } catch (FileNotFoundException e) {
//                e.printStackTrace();
        }
    }

    public RestaurantViewPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    //    https://stackoverflow.com/questions/33865445/gps-location-provider-requires-access-fine-location-permission-for-android-6-0

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pagerAdapter.getMapFragment().enableLocationFunctions();
            } else {
                Toast.makeText(DisplayRestaurantActivity.this,
                        "Unable to start. Please allow this application to use location services.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    // Activates alert message and progress bar if click "update"

    private static class StringHolder {
        public static String dateToStore = null;
    }
}
