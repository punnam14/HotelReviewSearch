package hotelapp.byhotel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.jettyServer.DatabaseHandler;

import java.io.FileReader;
import java.io.IOException;

/**
 * Manages the processing of hotel data.
 */
public class HotelManager {

    /**
     * Processes a list of hotel paths to populate the hotel data and database.
     *
     * @param hotelPath path to JSON file containing hotel data.
     */
    public void processHotel(String hotelPath) {
        Gson gson = new Gson();
        try (FileReader fr = new FileReader(hotelPath)) {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray jsonArr = jo.getAsJsonArray("sr");
            Hotel[] hotels = gson.fromJson(jsonArr, Hotel[].class);
            for (Hotel oneHotel : hotels) {
                DatabaseHandler.getInstance().addHotel(
                        oneHotel.getId(),
                        oneHotel.getName(),
                        oneHotel.getAddress(),
                        oneHotel.getCity(),
                        oneHotel.getState(),
                        oneHotel.getCountry(),
                        oneHotel.getLatitude(),
                        oneHotel.getLongitude());
            }
        } catch (IOException e) {
            System.out.println("Could not read the file: " + e);
        }
    }
}
