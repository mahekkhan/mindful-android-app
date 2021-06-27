package ca.cmpt276.cmpt276project.model;

/**
 * Location class models location of a restaurant, that includes
 * a physical address, latitude, and longitude.
 */
public class Location {

    private String address;
    private double latitude;
    private double longitude;

    // constructor
    protected Location(String physicalAddress, String city, Double latitude, Double longitude) {
        this.address = physicalAddress.replaceAll("\"", "") + ", "
                + city.replaceAll("\"", "");
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // ~~~~~~~~~~~ simple ~~~~~~~~~~~~
    // ~~~~~ getters and setters ~~~~~
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public String getAddress() {
        return address;
    }

    protected void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
