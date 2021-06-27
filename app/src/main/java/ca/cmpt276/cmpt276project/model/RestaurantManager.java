package ca.cmpt276.cmpt276project.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity;
import ca.cmpt276.cmpt276project.ui.SearchSettings;

import static android.content.Context.MODE_PRIVATE;
import static ca.cmpt276.cmpt276project.ui.DisplayRestaurantActivity.TAG;

/**
 * RestaurantManager is the class to access all the restaurants
 * and their necessary details. It has methods to read csv files
 * and store violations, restaurant details and all the inspection
 * on them.
 */
public class RestaurantManager {
    // Used to ensure restaurants are only loaded once
    private boolean isDataLoaded = false;

    private long oldUpdateTime = 0;

    public long getOldUpdateTime() {
        return oldUpdateTime;
    }

    public void setOldUpdateTime(long oldUpdateTime) {
        this.oldUpdateTime = oldUpdateTime;
    }

    public boolean getIsDataLoaded() {
        return isDataLoaded;
    }

    public void setIsDataLoaded(boolean isLoaded) {
        isDataLoaded = isLoaded;
    }

    public void getDate(String date){
        Log.e("received date", date);
    }

    public void getDataFromWeb(String data){
        Log.e("recevied data from Web", data);
    }

    // List of all the restaurants
    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>() {
        // Overridden indexOf to find the restaurant from the list using tracking number
        @Override
        public int indexOf(Object restaurantTrackingNumber) {
            if (restaurantTrackingNumber instanceof String) {
                for (int i = 0; i < this.size(); i++) {
                    String id = (String) restaurantTrackingNumber;
                    if (id.equals(restaurants.get(i).getTrackingNumber())) {
                        return i;
                    }
                }
            }
            return -1;
        }
    };

    private ArrayList<Violation> allViolations = new ArrayList<Violation>() {
        // Overriding to find a violation's index using only the code
        @Override
        public int indexOf (Object violationCode) {
            if (violationCode instanceof Integer) {
                for (int i = 0; i < this.size(); i++) {
                    Integer code = (Integer) violationCode;
                    if (code.equals(allViolations.get(i).getViolationCode())) {
                        return i;
                    }
                }
            }
            return -1;
        }
    };

    private ArrayList<String> violationNatureList = new ArrayList<>();

    // When user clicks on a restaurant, we store the position using this variable,
    // so that other activities can get the selected restaurant
    private int selectedRestaurant = 0;

    // Private object created as part of Singleton model
    private static RestaurantManager managerInstance = null;

    //Singleton:
    // Only way to have an object of this class will be via this function
    public static RestaurantManager getManagerInstance () {
        if (managerInstance == null) {
            managerInstance = new RestaurantManager();
        }
        return managerInstance;
    }

    // Constructor is private so that UI cannot create any new RestaurantManager
    private RestaurantManager() {
    }

    // Get all the violation types found in the violation-list
    public ArrayList<String> getViolationNatureList() {
        return violationNatureList;
    }

    // Reads all the violations from all_violations.csv,
    // and list them, so that it may be easier to find them.
    public void readViolationList (BufferedReader reader) {
        String line = "";
        try {
            reader.readLine();
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                addToViolationList(line);
            }
        } catch (Exception e) {
        }
    }

    private void addToViolationList(String line) {
        String[] eachViolation = line.split(",");

        Violation violation = new Violation(Integer.parseInt(eachViolation[0]));

        violation.setViolationType(eachViolation[1]);
        violation.setViolationDetails(eachViolation[2]);
        violation.setViolationRepetition(eachViolation[3]);
        if (eachViolation.length > 4) {
            violation.setNature(eachViolation[4]);
        } else {
            violation.setNature("miscellaneous");
        }

        if (!violationNatureList.contains(violation.getNature())) {
            violationNatureList.add(violation.getNature());
        }

        allViolations.add(violation);
    }

    // This function reads the restaurant details CSV file.
    // BufferedReader has to be passed from Android activities
    public void readRestaurantDetail (BufferedReader reader) {


        try {
            // Each line of CSV will be stored in this String
            String line = "";
            reader.readLine();
            int id = 0;
            while ((line = reader.readLine()) != null) {
//                count++;

                // line will be split by ",", into an array of String
                String[] eachDetailsOfRestaurant = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // Restaurant object created with the tracking number and name.
                Restaurant restaurant = new Restaurant(eachDetailsOfRestaurant[0], eachDetailsOfRestaurant[1], id);

//                Log.e("checking..............", count.toString() + " " + restaurant.getName());

                // Converting coordinates to double and creating Location object
                for (int i = 0; i < eachDetailsOfRestaurant.length; i++) {
                    Log.e(TAG, "" + i + ":" + eachDetailsOfRestaurant[i]);
                }
                Double latitude = Double.parseDouble(eachDetailsOfRestaurant[5]);
                Double longitude = Double.parseDouble(eachDetailsOfRestaurant[6]);
                Location location = new Location(eachDetailsOfRestaurant[2], eachDetailsOfRestaurant[3], latitude, longitude);

                // Setting Location of the restaurant and adding to the Arraylist
                restaurant.setLocation(location);
                restaurants.add(restaurant);
                id++;
            }
            Collections.sort(restaurants);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    //    Integer count = 0;
    public void readReports (BufferedReader reader) {
        try {
            // Each line of CSV will be stored in this String
            String line = "";
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // line will be split by ",", into an array of String
                String[] eachDetailOfReportLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                if (eachDetailOfReportLine.length == 0) {
                    continue;
                }

                // removing unnecessary quotation marks
                for (int i = 0; i < eachDetailOfReportLine.length; i++) {
                    eachDetailOfReportLine[i] = eachDetailOfReportLine[i].replaceAll("\"", "");
                }


                // finds restaurant using tracking code from restaurants' list
                Restaurant restaurant = null;
                for (Restaurant R : restaurants) {
                    if (R.getTrackingNumber().equals(eachDetailOfReportLine[0])) {
                        restaurant = R;
                        break;
                    }
                }

                if (restaurant == null) {
                    continue;
                }

//                    Log.e("checking..............", restaurant.getName());

                // setting other values
                Integer day = Integer.parseInt(eachDetailOfReportLine[1]);
                Report reportToAdd = new Report(day, eachDetailOfReportLine[2]);

                String thatDay = day.toString();
                Date thatDayCalendar = new SimpleDateFormat("yyyyMMdd").parse(thatDay);
                if (oldUpdateTime > 0) {
                    if (thatDayCalendar.getTime() > oldUpdateTime) {
                        restaurant.setHasUpdate(true);
                    }
                }


                Integer numCriticalIssues = Integer.parseInt(eachDetailOfReportLine[3]);
                reportToAdd.setCritIssues(numCriticalIssues);

                Integer numNonCriticalIssues = Integer.parseInt(eachDetailOfReportLine[4]);
                reportToAdd.setNonCritIssues(numNonCriticalIssues);

                try {
                    long daysSinceOccurrence = reportToAdd.getDaysSinceOccurrence();
                    if (daysSinceOccurrence <= 365) {
                        restaurant.addTotalCritViolations(numCriticalIssues);
                    }
                } catch (ParseException e) {
//                    e.printStackTrace();
                }

                // if there is any violation, then make a list of them
                // using processViolations method.
                // and set the list as restaurant's violation list
                if (eachDetailOfReportLine.length > 5) {

                    List<Violation> violationsOfOneInspection =
                            processViolations(eachDetailOfReportLine[5]);
                    reportToAdd.setViolations(violationsOfOneInspection);
                } else {
                    List<Violation> violationsOfOneInspection =
                            processViolations("");
                    reportToAdd.setViolations(violationsOfOneInspection);
                }
                if (eachDetailOfReportLine.length > 6) {
                    reportToAdd.setHazardLevel(eachDetailOfReportLine[6]);
                } else {
                    reportToAdd.setHazardLevel("Low");
                }

                restaurant.addReport(reportToAdd);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        // sort alphabetically, using overridden compareTo
        for (int i = 0; i < restaurants.size(); i++) {
            Collections.sort(restaurants.get(i).getReportsList());
        }
    }

    // Takes a string of all violations found in one inspection,
    // separates them and finds the violations in violations list.
    // Adds the found violation to an Arraylist of Violations, and
    // returns that list.
    private ArrayList<Violation> processViolations(String violationsRaw) {
        ArrayList<Violation> violationsInOneInspection = new ArrayList<>();
        if (violationsRaw.equals("")) {
            return violationsInOneInspection;
        }
        String[] violationsList = violationsRaw.split("\\|");
        for (int i = 0; i < violationsList.length; i++) {
            String eachViolation[] = violationsList[i].split(",");
            Integer violationCode;
            if (eachViolation[0].length() > 0) {
                violationCode = Integer.parseInt(eachViolation[0]);
                int index = allViolations.indexOf(violationCode);

                if (index == -1) {
                    addToViolationList(violationsList[i]);
                    index = allViolations.size() - 1;
                }

                violationsInOneInspection.add(allViolations.get(index));
            }
        }
        return violationsInOneInspection;
    }

    // Setter for the restaurant user is selecting
    public void setSelectedRestaurant(int selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }

    // This function returns the chosen Restaurant
    public Restaurant getSelectedRestaurant () {
        return restaurants.get(selectedRestaurant);
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public List<Restaurant> getFilteredRestaurants() {
        SearchSettings filters = SearchSettings.getSearchInstance();

        final String searchKey = filters.getSearchString();
        final String hazardLevel = filters.getHazardLevel();
        final Integer maxCritViolations = filters.getMaxCritViolations();
        boolean isOnlyFavourites = filters.isOnlyFavourites();

        ArrayList<Restaurant> restaurantsFiltered = new ArrayList<>();

        if (searchKey.equals("") && hazardLevel.equals("") && !isOnlyFavourites && maxCritViolations == null) {
            restaurantsFiltered = this.restaurants;
        } else {
            restaurantsFiltered.addAll(this.restaurants);
            restaurantsFiltered.removeIf(new Predicate<Restaurant>() {
                @Override
                public boolean test(Restaurant restaurant) {
                    return !restaurant.getName().toLowerCase()
                            .contains(searchKey.toLowerCase().trim());
                }
            });

            if (hazardLevel.equals("Low") || hazardLevel.equals("Medium") || hazardLevel.equals("High")) {
                restaurantsFiltered.removeIf(new Predicate<Restaurant>() {
                    @Override
                    public boolean test(Restaurant restaurant) {
                        return !restaurant.getLatestReportHazardLvl().toLowerCase()
                                .equals(hazardLevel.toLowerCase());
                    }
                });
            }

            // max critical violations
            if (maxCritViolations != null) {
                restaurantsFiltered.removeIf(new Predicate<Restaurant>() {
                    @Override
                    public boolean test(Restaurant restaurant) {
                        return restaurant.getTotalCritViolationInLastYear() > maxCritViolations;
                    }
                });
            }

/////////////////////// add favourites:
            if (isOnlyFavourites) {
                restaurantsFiltered.removeIf(new Predicate<Restaurant>() {
                    @Override
                    public boolean test(Restaurant restaurant) {
                        return !restaurant.isFavourite();
                    }
                });
            }
        }
        return restaurantsFiltered;
    }

    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}
