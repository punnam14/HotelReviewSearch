package server.jettyServer;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

public class DatabaseHandler {

    private static DatabaseHandler dbHandler = new DatabaseHandler("database.properties");
    private Properties config;
    private String uri = null;
    private Random random = new Random();

    /**
     * Constructs a DatabaseHandler with specified properties file.
     */
    private DatabaseHandler(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    /**
     * Returns the instance of the database handler.
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return dbHandler;
    }

    /**
     * Loads database configuration from a properties file.
     * @param propertyFile The file path of the properties file.
     * @return Loaded Properties object.
     */
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return config;
    }

    /**
     * Creates tables in the database if they do not already exist.
     * @return true if tables exist or are created successfully, false otherwise.
     */
    public boolean createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            List<String> tables = dbHandler.showTables();
            if(tables.contains("users") &&
                    tables.contains("hotels") &&
                    tables.contains("reviews") &&
                    tables.contains("favorites") &&
                    tables.contains("visited_links") &&
                    tables.contains("user_logins")){
                return true;
            }
            System.out.println("dbConnection successful");
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_USER_TABLE);
            statement.execute(PreparedStatements.CREATE_HOTEL_TABLE);
            statement.execute(PreparedStatements.CREATE_REVIEWS_TABLE);
            statement.execute(PreparedStatements.CREATE_FAVORITES_TABLE);
            statement.execute(PreparedStatements.CREATE_VISITED_LINKS_TABLE);
            statement.execute(PreparedStatements.CREATE_USER_LOGINS_TABLE);


        }
        catch (SQLException ex) {
             System.out.println("Coming from createTable " +ex);
        }
        return false;
    }

    /**
     * Lists all tables in the database.
     * @return A list of table names.
     */
    public List<String> showTables() {
        List<String> tables= new ArrayList<>();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement("SHOW TABLES;");
            ResultSet results = statement.executeQuery();
            while (results.next()){
                String databaseName = results.getString(1);
                tables.add(databaseName);
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return tables;
    }

    /**
     * Adds a new hotel to the database with the provided details.
     *
     */
    public void addHotel(String id, String name, String address, String city, String state, String country, String latitude, String longitude) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_HOTEL_SQL);
            statement.setString(1, id);
            statement.setString(2, name);
            statement.setString(3, address);
            statement.setString(4, city);
            statement.setString(5, state);
            statement.setString(6, country);
            statement.setString(7, latitude);
            statement.setString(8, longitude);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error adding hotel to database: " + e);
        }
    }

    /**
     * Adds a new review to the database with the provided details.
     *
     */
    public void addReview(String reviewId, String hotelId, String ratingOverall, String title, String reviewText, String userNickname, String reviewSubmissionTime) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEWS_SQL)) {

            statement.setString(1, reviewId);
            statement.setString(2, hotelId);
            statement.setString(3, ratingOverall);
            statement.setString(4, title);
            statement.setString(5, reviewText);
            statement.setString(6, userNickname);
            statement.setString(7, reviewSubmissionTime);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding review: " + e);
        }
    }


    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     */
    public void registerUser(String newuser, String newpass) {
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String usersalt = encodeHex(saltBytes, 32);
        String passhash = getHash(newpass, usersalt);
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                if(userExists(newuser)) return;
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            }
            catch(SQLException e) {
                System.out.println(e);
            }
        }
        catch (SQLException ex) {
            System.out.println("Coming from registerUser " +ex);
        }

    }

    /**
     * Checks if a user exists in the database based on the provided username.
     *
     * @param username The username to check in the database.
     * @return true if the user exists, false otherwise. Also returns false in case of any SQL errors.
     */

    public boolean userExists(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.USER_SQL);
            statement.setString(1, username.strip().trim().toLowerCase());
            ResultSet results = statement.executeQuery();
            boolean exists = results.next();
            statement.close();
            return exists;
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e);
            return false;
        }
    }

    /**
     * Authenticates a user by checking if the username and password hash match the ones stored in the database.
     *
     * @param username - The username of the user trying to log in.
     * @param password - The password of the user trying to log in.
     * @return true if authentication is successful, false otherwise.
     */
    public boolean authenticateUser(String username, String password) {
        String hashedPassword = getHash(password, fetchUserSalt(username));
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            ResultSet results = statement.executeQuery();
            boolean authenticated = results.next();
            statement.close();
            return authenticated;
        } catch (SQLException e) {
            System.out.println("Authentication failed for user " + username + " due to an error: " + e);
            return false;
        }
    }

    /**
     * Fetches the salt associated with a specific user from the database.
     *
     * @param username - The username of the user whose salt is to be fetched.
     * @return The salt string if present, else null.
     */
    private String fetchUserSalt(String username) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getString("usersalt");
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Fetching salt failed for user " + username + " due to an error: " + e);
        }
        return null;
    }

    /**
     * Retrieves all hotel names from the database.
     *
     * @return A list of hotel names. Returns an empty list in case of an error or if no hotels are found.
     */
    public List<String> getAllHotelNames() {
        List<String> hotelNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_NAMES);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                hotelNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel names: " + e);
        }
        return hotelNames;
    }

    /**
     * Retrieves hotel names from the database filtered by a search term.
     *
     * @param searchTerm The term used to filter hotel names. This term is matched against the hotel names.
     * @return A list of filtered hotel names.
     */
    public List<String> getFilteredHotelNames(String searchTerm) {
        List<String> filteredHotelNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.FILTERED_HOTEL)) {
            statement.setString(1, "%" + searchTerm + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    filteredHotelNames.add(resultSet.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching filtered hotel names: " + e);
        }
        return filteredHotelNames;
    }

    /**
     * Retrieves detailed information about a hotel by its name.
     *
     * @param hotelName The name of the hotel.
     * @return A map containing hotel information such as ID, name, address, city, state, and country.
     */
    public Map<String, String> getHotelInfo(String hotelName) {
        Map<String, String> hotelInfo = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.HOTEL_INFO)) {
            statement.setString(1, hotelName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                hotelInfo.put("id", resultSet.getString("id"));
                hotelInfo.put("name", resultSet.getString("name"));
                hotelInfo.put("address", resultSet.getString("address"));
                hotelInfo.put("city", resultSet.getString("city"));
                hotelInfo.put("state", resultSet.getString("state"));
                hotelInfo.put("country", resultSet.getString("country"));
                hotelInfo.put("latitude", resultSet.getString("latitude"));
                hotelInfo.put("longitude", resultSet.getString("longitude"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel information: " + e);
        }
        return hotelInfo;
    }

    /**
     * Retrieves all reviews for a specific hotel.
     *
     * @param hotelId The unique identifier of the hotel.
     * @return A list of maps, each containing details of a review.
     */
    public List<Map<String, String>> getReviewsForHotel(String hotelId) {
        List<Map<String, String>> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEWS_HOTEL)) {
            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Map<String, String> review = new HashMap<>();
                review.put("reviewId", resultSet.getString("review_id"));
                review.put("hotelId", resultSet.getString("hotel_id"));
                review.put("ratingOverall", resultSet.getString("rating_overall"));
                review.put("title", resultSet.getString("title"));
                review.put("reviewText", resultSet.getString("review_text"));
                review.put("userNickname", resultSet.getString("user_nickname"));
                review.put("reviewSubmissionTime", resultSet.getString("review_submission_time"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reviews: " + e);
        }
        return reviews;
    }

    /**
     * Calculates the average rating for a hotel based on its reviews.
     *
     * @param hotelId The unique identifier of the hotel.
     * @return The average rating as a double value rounded to two decimal places.
     *         Returns 0.0 if there are no reviews or in case of an error.
     */
    public double averageRating(String hotelId) {
        double averageRating = 0.0;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_AVG_RATING)) {
            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                averageRating = resultSet.getDouble("avgRating");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating average rating: " + e);
        }
        return averageRating;
    }

    /**
     * Retrieves the name of a hotel by its ID.
     *
     * @param hotelId The unique identifier of the hotel.
     * @return The name of the hotel as a string. Returns an empty string if no hotel is found or in case of an error.
     */
    public String getHotelName(String hotelId) {
        String hotelName = "";
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_HOTEL_NAME)) {

            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                hotelName = resultSet.getString("name");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel name: " + e);
        }
        return hotelName;
    }

    /**
     * Fetches the details of a review by its ID.
     *
     * @param reviewId The unique identifier of the review.
     * @return A Map containing the details of the review. Returns an empty map if no review is found or in case of an error.
     */
    public Map<String, String> getReviewById(String reviewId) {
        Map<String, String> review = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_REVIEW_BY_ID)) {
            statement.setString(1, reviewId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                review.put("reviewId", resultSet.getString("review_id"));
                review.put("hotelId", resultSet.getString("hotel_id"));
                review.put("ratingOverall", resultSet.getString("rating_overall"));
                review.put("title", resultSet.getString("title"));
                review.put("reviewText", resultSet.getString("review_text"));
                review.put("userNickname", resultSet.getString("user_nickname"));
                review.put("reviewSubmissionTime", resultSet.getString("review_submission_time"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching review: " + e);
        }
        return review;
    }

    /**
     * Updates the content of a review.
     *
     * @param reviewId  The unique identifier of the review to be updated.
     * @param newTitle  The new title for the review.
     * @param newText   The new text content for the review.
     */
    public void updateReview(String reviewId, String newTitle, String newText) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.UPDATE_REVIEW)) {
            statement.setString(1, newTitle);
            statement.setString(2, newText);
            statement.setString(3, reviewId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating review: " + e);
        }
    }

    /**
     * Retrieves the hotel ID associated with a given review.
     *
     * @param reviewId The unique identifier of the review.
     * @return The ID of the hotel associated with the review. Returns null if no hotel is found or in case of an error.
     */
    public String getHotelIdByReviewId(String reviewId) {
        String hotelId = "";
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_HOTEL_BY_REVIEW)) {
            statement.setString(1, reviewId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                hotelId = resultSet.getString("hotel_id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel ID for review: " + e);
        }
        return hotelId;
    }

    /**
     * Deletes a review from the database.
     *
     * @param reviewId  The unique identifier of the review to be deleted.
     * @param username  The username of the user who posted the review.
     * @return The name of the hotel associated with the deleted review,
     *         which can be used for redirect purposes. Returns null if the operation fails.
     */
    public String deleteReview(String reviewId, String username) {
        String hotelId = getHotelIdByReviewId(reviewId);
        if (hotelId == null) {
            return null;
        }
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW)) {
            statement.setString(1, reviewId);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return getHotelName(hotelId);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting review: " + e);
        }
        return null;
    }

    /**
     * Retrieves the hotel ID from the database based on the hotel name.
     *
     * @param hotelName The name of the hotel.
     * @param conn      The database connection object.
     * @return The hotel ID if found, -1 otherwise.
     */
    private int getHotelIdByName(String hotelName, Connection conn) {
        try (PreparedStatement statement = conn.prepareStatement(PreparedStatements.GET_HOTEL_ID_BY_NAME)) {
            statement.setString(1, hotelName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves the user ID from the database based on the username.
     *
     * @param username The username of the user.
     * @param conn     The database connection object.
     * @return The user ID if found, -1 otherwise.
     */
    private int getUserIdByUsername(String username, Connection conn) {
        try (PreparedStatement statement = conn.prepareStatement(PreparedStatements.GET_USER_ID_BY_USERNAME)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("userid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a hotel to the user's favorites in the database.
     *
     * @param user      The username of the user.
     * @param hotelName The name of the hotel to add to favorites.
     */
    public void addFavorite(String user, String hotelName) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.ADD_FAVORITE)) {
            int hotelId = getHotelIdByName(hotelName, connection);
            int userId = getUserIdByUsername(user,connection);
            if (hotelId != -1) {
                statement.setInt(1, userId);
                statement.setInt(2, hotelId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a hotel from the user's favorites in the database.
     *
     * @param user      The username of the user.
     * @param hotelName The name of the hotel to remove from favorites.
     */
    public void removeFavorite(String user, String hotelName) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.REMOVE_FAVORITE)) {
            int hotelId = getHotelIdByName(hotelName, connection);
            int userId = getUserIdByUsername(user,connection);
            if (hotelId != -1) {
                statement.setInt(1, userId);
                statement.setInt(2, hotelId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a hotel is a favorite of the user.
     *
     * @param user      The username of the user.
     * @param hotelName The name of the hotel to check.
     * @return true if the hotel is a favorite of the user, false otherwise.
     */
    public boolean checkFavoriteHotelOfUser(String user, String hotelName){
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.CHECK_FAVORITE_HOTEL_OF_USER)) {
            int hotelId = getHotelIdByName(hotelName, connection);
            int userId = getUserIdByUsername(user,connection);
            if (hotelId != -1 && userId != -1) {
                statement.setInt(1, userId);
                statement.setInt(2, hotelId);
                ResultSet rs = statement.executeQuery();
                if(rs.next()) return true;
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a list of favorite hotels for a user.
     *
     * @param user The username of the user.
     * @return A list of favorite hotels.
     */
    public ArrayList<String> GetFavoriteHotelsOfUser(String user){
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_FAVORITE_HOTELS_BY_USER)) {
            ArrayList<String> ret = new ArrayList<>();
            int userId = getUserIdByUsername(user,connection);
            if (userId != -1) {
                statement.setInt(1, userId);
                ResultSet rs = statement.executeQuery();
                while(rs.next()){
                    ret.add(getHotelName(rs.getString("hotelId")));
                }
                return ret;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clears all favorite hotels of a user.
     *
     * @param user The username of the user.
     */
    public void clearFavorites(String user){
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement statement = connection.prepareStatement(PreparedStatements.REMOVE_FAVORITES_OF_USER)) {
            ArrayList<String> ret = new ArrayList<>();
            int userId = getUserIdByUsername(user,connection);
            if (userId != -1) {
                statement.setInt(1, userId);
                statement.executeUpdate();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Records a user's visit to an Expedia link.
     *
     * @param user       The username of the user.
     * @param expediaUrl The URL of the Expedia page visited.
     */
    public void recordVisit(String user, String expediaUrl){
        try (Connection conn = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement stmt = conn.prepareStatement(PreparedStatements.INSERT_USER_LINK_VISIT)){
            int userId = getUserIdByUsername(user,conn);
            stmt.setInt(1, userId);
            stmt.setString(2, expediaUrl);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all recorded visits to Expedia links by a user.
     *
     * @param user The username of the user.
     * @return A map of visit timestamps and corresponding Expedia URLs.
     */
    public HashMap<String,String> getLinkVisitsOfUser(String user){
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement stmt = connection.prepareStatement(PreparedStatements.GET_LINKS_OF_USER)) {
            HashMap<String,String> ret = new HashMap<>();
            int userId = getUserIdByUsername(user,connection);
            if (userId != -1) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    String exp = rs.getString("expediaUrl");
                    String ts = rs.getTimestamp("visitTimestamp").toString();
                    ret.put(ts,exp);
                }
                return ret;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Clears all recorded Expedia link visits of a user.
     *
     * @param user The username of the user.
     */
    public void clearExpediaLinks(String user){
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement stmt = connection.prepareStatement(PreparedStatements.REMOVE_EXPEDIA_LINKS_OF_USER)) {
            ArrayList<String> ret = new ArrayList<>();
            int userId = getUserIdByUsername(user,connection);
            if (userId != -1) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the last login time of a user.
     *
     * @param username  The username of the user.
     * @param loginTime The timestamp of the last login.
     */
    public void storeLastLogin(String username, Timestamp loginTime) {
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement stmt = connection.prepareStatement(PreparedStatements.INSERT_LAST_LOGIN_TIME)) {
            int userId = getUserIdByUsername(username,connection);
            stmt.setInt(1, userId);
            stmt.setTimestamp(2, loginTime);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the last login time of a user.
     *
     * @param username The username of the user.
     * @return The timestamp of the last login.
     */
    public Timestamp getLastLogin(String username) {
        Timestamp lastLoginTime = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
             PreparedStatement stmt = connection.prepareStatement(PreparedStatements.GET_LAST_LOGIN_TIME)) {
            int userId = getUserIdByUsername(username, connection);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastLoginTime = rs.getTimestamp("lastLoginTime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastLoginTime;
    }
}

