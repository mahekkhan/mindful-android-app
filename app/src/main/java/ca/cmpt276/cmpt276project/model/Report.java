package ca.cmpt276.cmpt276project.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/**
 * Report class represents each inspection found from the
 * inspections list .csv.
 * It has a date, type, number of critical and non-critical
 * issues, hazard level, and a list of violations, if any.
 * This class lets the user to know how long ago the inspection
 * occurred, as well as the date in standard format.
 * It also implements Comparable to enable sorting by date in
 * descending order.
 */
public class Report implements Comparable {

    // holds one inspection report and its details
    // report holds:
    //  - date of inspection
    //  - # critical issues
    //  - # non-critical issues
    //  - hazard level
    //  - details about inspection

    public static final String REPORT_KEY = "Report_intent_key";
    private Integer date;
    private String type;
    private int critIssues;
    private int nonCritIssues;
    private String hazardLevel;
    private List<Violation> violations;

    // Constructor
    protected Report(int date, String type){
        this.date = date;
        this.type = type;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~ simple ~~~~~~~~~~~~
    // ~~~~~ getters and setters ~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Returns the integer date found in .csv
    protected Integer getDate() {
        return date;
    }

    protected void setDate(int date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public int getCritIssues() {
        return critIssues;
    }

    protected void setCritIssues(int critIssues) {
        this.critIssues = critIssues;
    }

    public int getNonCritIssues() {
        return nonCritIssues;
    }

    protected void setNonCritIssues(int nonCritIssues) {
        this.nonCritIssues = nonCritIssues;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    protected void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    protected void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    // Sorts reports in descending order by date
    // so that latest one is at the top
    @Override
    public int compareTo (Object otherReport) {
        int comparison = ((Report) otherReport).getDate().compareTo(this.getDate());
        return comparison;
    }

    /////////////////////////////
    //////  Process Date  ///////
    ///  As well as getters  ////

    public String getFullDate () {
        String returnMonthAndYear;
        Integer inspectionDay = this.getDate();

        int year = inspectionDay / 10000;
        inspectionDay = inspectionDay % 10000;

        int month = inspectionDay / 100;
        inspectionDay = inspectionDay % 100;
        // inspectionDay is the day number now
        returnMonthAndYear = getMonth(month) + " " + inspectionDay + ", " + year;
        return returnMonthAndYear;
    }

    public String getHowLongAgoOccurredFormatted() {
        try {
            String dayToReturn = "";
            long time = getDaysSinceOccurrence();

            // Determining return value based on user's requirements
            if (time < 30) {
                dayToReturn = time + " days";
            } else if (time < 365) {
                dayToReturn = getDayForWithinYear();
            } else {
                dayToReturn = getDayForAfterAYear();
            }

            return dayToReturn;
        } catch (Exception e) {
            return "None";
        }
    }

    protected long getDaysSinceOccurrence() throws ParseException {
        // Today
        Calendar today = Calendar.getInstance();

        String thatDay = this.date.toString();
        // creating Date from String
        Date thatDayCalendar = new SimpleDateFormat("yyyyMMdd").parse(thatDay);

        // Days difference between today and last inspection date
        // converts milliseconds to days (1 day = 86400000 millis)
        long time = (today.getTimeInMillis()
                - thatDayCalendar.getTime()) / 86400000;
        time = Math.abs(time);
        return time;
    }

    private String getDayForAfterAYear() {
        String returnMonthAndYear;

        Integer inspectionDay = this.getDate();

        int year = inspectionDay / 10000;
        inspectionDay = inspectionDay % 10000;

        int month = inspectionDay / 100;

        returnMonthAndYear = getMonth(month) + " " + year;
        return returnMonthAndYear;
    }

    private String getDayForWithinYear() {
        String returnDay
                ;
        Integer inspectionDay = this.getDate();
        // Ignoring year
        inspectionDay = inspectionDay % 10000;

        int month = inspectionDay / 100;
        inspectionDay = inspectionDay % 100;

        returnDay = getMonth(month);
        returnDay += " " + inspectionDay;
        return returnDay;
    }

    private String getMonth(int month) {
        String returnDay = "";
        switch (month) {
            case 1:
                returnDay = "January";
                break;
            case 2:
                returnDay = "February";
                break;
            case 3:
                returnDay = "March";
                break;
            case 4:
                returnDay = "April";
                break;
            case 5:
                returnDay = "May";
                break;
            case 6:
                returnDay = "June";
                break;
            case 7:
                returnDay = "July";
                break;
            case 8:
                returnDay = "August";
                break;
            case 9:
                returnDay = "September";
                break;
            case 10:
                returnDay = "October";
                break;
            case 11:
                returnDay = "November";
                break;
            case 12:
                returnDay = "December";
                break;
            default:
                break;
        }
        return returnDay;
    }

}
