package ca.cmpt276.cmpt276project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.RestaurantManager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Handles Alert Dialog and Progress Bar that prompts user to update data as soon as app opens up
 */
// https://www.javatpoint.com/android-progressbar-example
public class DisplayUpdateAlert extends AppCompatDialogFragment implements ResponseListener {
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private long dataSize = 0;
    public boolean isUpdate = false;

    private final String NAME_TIME = "lastUpdateTimes";
    private final String TAG_RESTAURANT = "lastRestaurantTime";
    private final String TAG_INSPECTION = "lastInspectionTime";
    private String restaurantFileNameString = "restaurants.csv";
    private String inspectionFileNameString = "inspection_list.csv";

    public Context context;
    public Context applicationContext;


    public DisplayUpdateAlert(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create the view to show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.activity_display_update_alert, null);

        // Build the alert dialog
        String title = getResources().getString(R.string.update_alert_prompt);
        final String accept = getResources().getString(R.string.update_alert_accept);
        String decline = getResources().getString(R.string.update_alert_decline);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton(accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), accept, Toast.LENGTH_SHORT).show();
                        //update data if user agrees
//                        isUpdate("Update");
                        updateIsUpdate(true);
                        Log.e("checkInspectionTime", "user Agrees");

                        final String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
                        final String inspectionURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
                        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());

                        DisplayUpdateAlert.this.context = getContext();
                        Calendar today = Calendar.getInstance();
                        // String restaurantDataURL = resources.getString("url");
                        getRestaurantRawDataURL(restaurantURL, requestQueue);
                        getInspectionRawDataURL(inspectionURL, requestQueue);
//                        DownloadAndRead downloadAndRead = new DownloadAndRead(DisplayUpdateAlert.this);
//                        downloadAndRead.execute();

                        String destinationRestaurant = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
                        String destinationReport = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
                        readRestaurantAndReportFiles(destinationRestaurant, destinationReport);

                        // save date here
                        storeToSharedPref(NAME_TIME, TAG_INSPECTION, today.getTimeInMillis());
                        /////////////////////
                        long checkTime = getFromSharedPref(NAME_TIME, TAG_INSPECTION);
                        Log.e("testing.......in yes.............", checkTime + "");
                        callProgressDialog();
                    }
                })
                .setNegativeButton(decline, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Do not update", Toast.LENGTH_SHORT).show();
                        updateIsUpdate(false);
                        String destinationRestaurant = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
                        String destinationReport = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
                        readRestaurantAndReportFiles(destinationRestaurant, destinationReport);
                        dialog.cancel();
                    }
                })
                .create();
    }

    @Override
    public void receiveResponse(final String save, final String tag) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                readRestaurantAndReportFiles(save, tag);
            }
        });
    }


    class DownloadAndRead extends AsyncTask<String, String, String> {

        private ResponseListener responseListener;

        public DownloadAndRead(ResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        protected String doInBackground(String... strings) {
            final String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
            final String inspectionURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
            final RequestQueue requestQueue = Volley.newRequestQueue(DisplayUpdateAlert.this.context);

            getRestaurantRawDataURL(restaurantURL, requestQueue);
            getInspectionRawDataURL(inspectionURL, requestQueue);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            final String restaurantURL = "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
            final String inspectionURL = "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
//            responseListener.receiveResponse(restaurantURL, inspectionURL);
            readRestaurantAndReportFiles(restaurantURL, inspectionURL);
        }
    }

    private void updateIsUpdate(boolean flag){
      this.isUpdate = flag;
    }

    private void callProgressDialog(){
        // Creating progress bar dialog
        progressBar = new ProgressDialog(getContext());
        // Cancels update when press outside of progress bar dialog box
        progressBar.setCancelable(true);
        progressBar.setMessage("Updating Data ...\n (click outside box to cancel update)");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();

        // Reset the progress bar and dataSize status
        progressBarStatus = 0;
        dataSize = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // Performing operation
                    progressBarStatus = doOperation();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Updating the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                // Performing operation if file is downloaded,
                if (progressBarStatus >= 100) {
                    // Sleeping for 1 second after operation completed
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Close the progress bar dialog
                    progressBar.dismiss();
                }
            }
        }).start();
    }

    public int doOperation() {
        // The range of ProgressDialog starts from 0 to 10000
        while (dataSize <= 10000) {
            dataSize++;
            if (dataSize == 1000) {
                return 10;
            } else if (dataSize == 2000) {
                return 20;
            } else if (dataSize == 3000) {
                return 30;
            } else if (dataSize == 4000) {
                return 40; // you can add more else if
            }
        }
        return 100;
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
                        Context context = DisplayUpdateAlert.this.applicationContext;
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
                        Context context = DisplayUpdateAlert.this.applicationContext;
                        String destinationFile = context.getFilesDir().getPath() + File.separator + inspectionFileNameString;
                        try {
                            CopyStringToInternalStorage(rawData, destinationFile);
                            String destinationRestaurant = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + restaurantFileNameString;
                            String destinationReport = getContext().getApplicationContext().getFilesDir().getPath() + File.separator + inspectionFileNameString;
                            readRestaurantAndReportFiles(destinationRestaurant, destinationReport);
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

    protected void getRestaurantRawDataURL(String url, final RequestQueue requestQueue){
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

                            getRestaurantFromWeb(inspectionURL, requestQueue);

                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("getRestaurantRawData Error", error.toString());
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

    private void CopyStringToInternalStorage( String rawData, String destinationFile) throws IOException {
        InputStream inputStream =  new ByteArrayInputStream(rawData.getBytes());
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        byte[] buffer = rawData.getBytes();
        int length = inputStream.read(buffer);
        while(length > 0){
            outputStream.write(buffer, 0, length);
            length = inputStream.read(buffer);
        }
        Log.e("copy server's data after download", "......... succeeded");

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    private void storeToSharedPref(String name, String key,  Long time) {
        SharedPreferences prefs = context.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, time);
        Log.e("testing....................", time + "");

        // uncomment this save changes
        editor.commit();
    }

    private Long getFromSharedPref(String name, String key){
        SharedPreferences prefs = context.getSharedPreferences(name, MODE_PRIVATE);
        Long time = prefs.getLong(key, 0);

        return time;
    }

    private void readRestaurantAndReportFiles(String destinationRestaurant, String destinationReport) {
        File restaurantFile = new File(destinationRestaurant);
        File reportsFile = new File(destinationReport);
        try {
            Log.e("reading file in DisplayUpdateAlert before", "..............succeeded");
            RestaurantManager restaurantManager = RestaurantManager.getManagerInstance();
            restaurantManager.readRestaurantDetail(new BufferedReader(new FileReader(restaurantFile)));
            Log.e("reading file in DisplayUpdateAlert after rest", "..............succeeded");
            restaurantManager.readReports(new BufferedReader(new FileReader(reportsFile)));
            Log.e("reading file in DisplayUpdateAlert after insp", "..............succeeded");
        } catch (FileNotFoundException e) {
            Log.e("reading file in DisplayUpdateAlert", "..............failed");
//                e.printStackTrace();
        }
    }
}

