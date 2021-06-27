package ca.cmpt276.cmpt276project.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Restaurant class models each restaurant found in the list.
 * It has a tracking number, name, location details, and a list
 * of all the inspections. This class implements Comparable
 * interface to let a list of Restaurants be sorted using
 * Collections class.
 */
public class Restaurant implements Comparable {
    public static final String RESTAURANT_KEY = "Restaurant_intent_key";

    // Each restaurant class has its own:
    // inspection class
    //  - has lists of all reports of the restaurant
    //  - has date of last inspection ( in String format )
    //  - each report contains:
    //      - hazard level
    //      - date
    //      - crit issues and non crit issues

    // location class

    private String trackingNumber;
    private String name;
    private Location location;
    private int criticalViolationsInOneYear = 0;
    private ArrayList<Report> reportsList = new ArrayList<>();
    private boolean isFavourite = false;
    private boolean hasUpdate = false;

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    private int id;

    protected Restaurant(String trackingNumber, String name, int id) {
        this.trackingNumber = trackingNumber;
        this.name = name.replaceAll("\"", "");
        this.id = id;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public int getId() {
        return id;
    }

    public Location getLocation(){
        return location;
    }

    public String getLatestReportHazardLvl(){
        if (reportsList.size() > 0) {
            Report report = reportsList.get(0);
            return report.getHazardLevel();
        } else {
            return "Low";
        }
    }

    public String getLatestInspectionDaysDifference() {
        try {
            return reportsList.get(0).getHowLongAgoOccurredFormatted();
        } catch (Exception e) {
            return "None";
        }
    }

    public int getSumTotalIssuesInLastInspection () {
        if (reportsList.size() > 0) {
            return reportsList.get(0).getCritIssues()
                    + reportsList.get(0).getNonCritIssues();
        } else {
            return 0;
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~ simple ~~~~~~~~~~~~
    // ~~~~~ getters and setters ~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public String getTrackingNumber() {
        return trackingNumber;
    }

    protected void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int compareTo(Object otherRestaurant) {
        int comparison = this.getName().compareTo(((Restaurant)otherRestaurant).getName());
        return comparison;
    }

    protected void addReport(Report reportToAdd) {
        this.reportsList.add(reportToAdd);
    }

    public List<Report> getReportsList() {
        return reportsList;
    }

    public void addTotalCritViolations(Integer numCriticalIssues) {
        this.criticalViolationsInOneYear += numCriticalIssues;
    }

    public int getTotalCritViolationInLastYear() {
        return this.criticalViolationsInOneYear;
    }
}
