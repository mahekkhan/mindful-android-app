package ca.cmpt276.cmpt276project.ui;

public class SearchSettings {
    public static SearchSettings searchInstance = null;
    private String searchString = "";
    private String hazardLevel = "";
    private Integer maxCritViolations = null;
    private boolean onlyFavourites = false;

    public static SearchSettings getSearchInstance() {
        if (searchInstance == null) {
            searchInstance = new SearchSettings();
        }
        return searchInstance;
    }

    private SearchSettings() {
//        this.searchString = "";
//        this.hazardLevel = "";
//        this.maxCritViolations = 0;
//        this.onlyFavourites = false;
    }

    public void setSearchSettings(String searchString, String hazardLevel, String maxCritViolations,
                                  boolean onlyFavourites) {
        this.searchString = searchString;
        this.hazardLevel = reformatHazardLevel(hazardLevel);
        this.maxCritViolations = parseStringAsInt(maxCritViolations);
        this.onlyFavourites = onlyFavourites;
    }

    private Integer parseStringAsInt(String maxCritViolations) {
        if (maxCritViolations.equals("")) {
            return null;
        } else {
            return Integer.parseInt(maxCritViolations);
        }
    }

    private String reformatHazardLevel(String hazardLevel) {
        return hazardLevel;
    }

    public String getSearchString() {
        return searchString;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public Integer getMaxCritViolations() {
        return maxCritViolations;
    }

    public boolean isOnlyFavourites() {
        return onlyFavourites;
    }

    public void clear() {
        this.searchString = "";
        this.hazardLevel = "";
        this.maxCritViolations = null;
        this.onlyFavourites = false;
    }

    @Override
    public String toString() {
        return "Search String: " + searchString + "\n"
                + "Hazard Level: " + hazardLevel + "\n"
                + "Max Crit Violations: " + maxCritViolations + "\n"
                + "Only Favourites: " + onlyFavourites;
    }
}
