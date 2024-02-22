package server.jettyServer;

public class PreparedStatements {
    /** Prepared Statements  */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS users(" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    public static final String CREATE_HOTEL_TABLE =
            "CREATE TABLE IF NOT EXISTS hotels (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "address VARCHAR(255), " +
                    "city VARCHAR(255), " +
                    "state VARCHAR(255), " +
                    "country VARCHAR(255), " +
                    "latitude VARCHAR(255), " +
                    "longitude VARCHAR(255));";

    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE IF NOT EXISTS reviews (" +
                    "    id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "    review_id VARCHAR(255)," +
                    "    hotel_id VARCHAR(255) NOT NULL," +
                    "    rating_overall VARCHAR(10)," +
                    "    title VARCHAR(255)," +
                    "    review_text TEXT," +
                    "    user_nickname VARCHAR(255)," +
                    "    review_submission_time VARCHAR(50)," +
                    "    FOREIGN KEY (hotel_id) REFERENCES hotels(id)" +
                    ");";

    public static final String CREATE_FAVORITES_TABLE =
            "CREATE TABLE IF NOT EXISTS favorites (" +
                    "userId INT NOT NULL, " +
                    "hotelId VARCHAR(255) NOT NULL, " +
                    "PRIMARY KEY (userId, hotelId));";

    public static final String CREATE_VISITED_LINKS_TABLE =
            "CREATE TABLE IF NOT EXISTS visited_links (" +
                    "userid INT, " +
                    "expediaUrl VARCHAR(255)," +
                    "visitTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";


    public static final String CREATE_USER_LOGINS_TABLE =
            "CREATE TABLE IF NOT EXISTS user_logins (" +
                    "userId INT PRIMARY KEY, " +
                    "lastLoginTime TIMESTAMP);";

    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?);";

    public static final String USER_SQL =
            "SELECT username FROM users WHERE username = ?";

    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?";

    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?";

    public static final String INSERT_HOTEL_SQL =
            "INSERT INTO hotels (id, name, address, city, state, country, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String INSERT_REVIEWS_SQL =
            "INSERT INTO reviews (review_id, hotel_id, rating_overall, title, review_text, user_nickname, review_submission_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String DELETE_REVIEW =
            "DELETE FROM reviews WHERE review_id = ? AND user_nickname = ?";

    public static final String GET_HOTEL_BY_REVIEW =
            "SELECT hotel_id FROM reviews WHERE review_id = ?";

    public static final String UPDATE_REVIEW =
            "UPDATE reviews SET title = ?, review_text = ? WHERE review_id = ?";

    public static final String GET_REVIEW_BY_ID =
            "SELECT * FROM reviews WHERE review_id = ?";

    public static final String GET_HOTEL_NAME =
            "SELECT name FROM hotels WHERE id = ?";

    public static final String GET_AVG_RATING =
            "SELECT ROUND(AVG(CAST(rating_overall AS FLOAT)), 2) AS avgRating FROM reviews WHERE hotel_id = ?";

    public static final String GET_REVIEWS_HOTEL =
            "SELECT * FROM reviews WHERE hotel_id = ? ORDER BY review_submission_time DESC";

    public static final String HOTEL_INFO =
            "SELECT * FROM hotels WHERE name = ?";

    public static final String FILTERED_HOTEL =
            "SELECT name FROM hotels WHERE name LIKE ?";

    public static final String GET_NAMES =
            "SELECT name FROM hotels";

    public static final String ADD_FAVORITE =
            "INSERT INTO favorites (userId, hotelId) VALUES (?, ?);";

    public static final String REMOVE_FAVORITE =
            "DELETE FROM favorites WHERE userId = ? AND hotelId = ?;";

    public static final String REMOVE_FAVORITES_OF_USER =
            "DELETE FROM favorites WHERE userId = ?;";

    public static final String GET_FAVORITE_HOTELS_BY_USER =
            "SELECT * from favorites where userid = ?";

    public static final String CHECK_FAVORITE_HOTEL_OF_USER =
            "SELECT * from favorites where userid = ? AND hotelId = ?";

    public static final String GET_HOTEL_ID_BY_NAME =
            "SELECT id FROM hotels WHERE name = ?;";

    public static final String GET_USER_ID_BY_USERNAME =
            "select userid from users where username = ?;";

    public static final String INSERT_USER_LINK_VISIT =
            "INSERT INTO visited_links (userid, expediaUrl) VALUES (?, ?)";
    public static final String GET_LINKS_OF_USER =
            "SELECT * from visited_links where userId = ?";
    public static final String REMOVE_EXPEDIA_LINKS_OF_USER =
            "DELETE FROM visited_links WHERE userId = ?;";

    public static final String INSERT_LAST_LOGIN_TIME =
            "INSERT INTO user_logins (userId, lastLoginTime) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE lastLoginTime = VALUES(lastLoginTime)";

    public static final String GET_LAST_LOGIN_TIME =
            "SELECT lastLoginTime FROM user_logins WHERE userId = ?";
}
