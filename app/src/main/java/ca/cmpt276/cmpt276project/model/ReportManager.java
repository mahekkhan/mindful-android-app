package ca.cmpt276.cmpt276project.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReportManager {

    // Manages reports for a single restaurant
    // use this to add reports to a list of reports

    private List<Report> Reports = new ArrayList<>();

    public void add(Report report) {
        Reports.add(report);
    }

    public Report get(int index) {
        return Reports.get(index);
    }

    public Iterator<Report> iterator() {
        return Reports.iterator();
    }

    public List<Report> getReports() {
        return Reports;
    }

}
