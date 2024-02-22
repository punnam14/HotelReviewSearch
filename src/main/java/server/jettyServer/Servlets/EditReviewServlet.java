package server.jettyServer.Servlets;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Servlet for handling the editing of reviews.
 */

public class EditReviewServlet extends HttpServlet {

    /**
     * Handles the GET request to display the edit review form.
     * Verifies user login and permissions before showing the form.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        String username = (String) session.getAttribute("username");
        String reviewId = request.getParameter("reviewId");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        Map<String, String> review = dbHandler.getReviewById(reviewId);
        String hotelId = review.get("hotelId");
        String hotelName = dbHandler.getHotelName(hotelId);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("review", review);
        context.put("hotelName", hotelName);
        if (!review.get("userNickname").equals(username)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        PrintWriter out = response.getWriter();
        Template template = ve.getTemplate("templates/editReview.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }

    /**
     * Handles the POST request to update a review.
     * Updates the review in the database and redirects to the reviews page.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String reviewId = request.getParameter("reviewId");
        String newTitle = request.getParameter("reviewTitle");
        String newText = request.getParameter("reviewText");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.updateReview(reviewId, newTitle, newText);
        String hotelId = dbHandler.getHotelIdByReviewId(reviewId);
        String hotelName = dbHandler.getHotelName(hotelId);
        response.sendRedirect("/reviews?name=" + hotelName);
    }
}
