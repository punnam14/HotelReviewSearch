package hotelapp.byhotel;

import com.google.gson.annotations.SerializedName;

/**
 * The Hotel class represents a hotel with various properties such as ID, name and address.
 */
public class Hotel{

    private final String id;

    @SerializedName(value = "f")
    private final String name;

    @SerializedName(value = "ad")
    private final String address;

    @SerializedName(value = "ci")
    private final String city;

    @SerializedName(value = "pr")
    private final String state;

    @SerializedName(value = "c")
    private final String country;

    @SerializedName(value = "ll")
    private LatLong latlong;

    /**
     * The LatLong inner class represents the latitude and longitude coordinates
     * of the hotel.
     */
    private class LatLong {
        @SerializedName(value = "lat")
        private String latitude;

        @SerializedName(value = "lng")
        private String longitude;

    }


    /**
     * Constructs a new Hotel with the given properties.
     *
     * @param id        The unique identifier for the hotel.
     * @param name      The name of the hotel.
     * @param address   The address of the hotel.
     * @param city      The city where the hotel is located.
     * @param state     The state where the hotel is located.
     * @param country   The country where the hotel is located.
     */
    public Hotel(String id, String name, String address, String city, String state, String country, String latitude, String longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.latlong = new LatLong();
        this.latlong.latitude = latitude;
        this.latlong.longitude = longitude;
    }

    /**
     * Returns a string representation of the hotel
     * @return string representation of this hotel
     */
    @Override
    public String toString() {
        return System.lineSeparator() + "********************" + System.lineSeparator() +
                name + ": " + id + System.lineSeparator() +
                address + System.lineSeparator() +
                city + ", " + state + System.lineSeparator() + latlong.latitude + ", " + latlong.longitude + System.lineSeparator() ;
    }

    /**
     * Gets the ID of the hotel.
     *
     * @return The ID of the hotel.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the hotel.
     *
     * @return Name of the hotel.
     */
    public String getName() {
        String hotelName = name.replace("&", "and").replace("'","");
        return hotelName;
    }

    /**
     * Retrieves the address of the hotel.
     *
     * @return Address of the hotel.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Retrieves the city where the hotel is located.
     *
     * @return City of the hotel.
     */
    public String getCity() {
        return city;
    }

    /**
     * Retrieves the state where the hotel is located.
     *
     * @return State of the hotel.
     */
    public String getState() {
        return state;
    }

    /**
     * Retrieves the country where the hotel is located.
     *
     * @return Country of the hotel.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Retrieves the latitude coordinate of the hotel.
     *
     * @return Latitude of the hotel.
     */
    public String getLatitude() {
        return latlong.latitude;
    }

    /**
     * Retrieves the longitude coordinate of the hotel.
     *
     * @return Longitude of the hotel.
     */
    public String getLongitude() {
        return latlong.longitude;
    }
}
