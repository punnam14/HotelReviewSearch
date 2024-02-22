package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SubmitReviewServlet extends HttpServlet {

    /**
     * Handles the POST request to store new review data into database.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        String hotelId = request.getParameter("hotelId");
        String hotelName = dbHandler.getHotelName(hotelId);
        String reviewTitle = request.getParameter("reviewTitle");
        String reviewText = request.getParameter("reviewText");
        String rating = request.getParameter("rating");
        String username = (String) session.getAttribute("username");
        String reviewId = generateReviewId();
        String submissionTime = getCurrentDateTime();
        dbHandler.addReview(reviewId, hotelId, rating, reviewTitle, reviewText, username, submissionTime);
        response.sendRedirect("/reviews?name=" + hotelName);
    }


    /**
     * Generates a unique review ID.
     * This method uses {@link UUID#randomUUID()} to generate a unique identifier
     * which can be used as a review ID.
     *
     * @return A string representing a unique review ID.
     */
    private String generateReviewId() {
        return UUID.randomUUID().toString();
    }



    /**
     * Gets the current date and time.
     * This method returns the current date and time formatted as "yyyy-MM-dd HH:mm:ss".
     * It uses SimpleDateFormat for formatting the date.
     *
     * @return A string representing the current date and time.
     */
    private static String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

}
