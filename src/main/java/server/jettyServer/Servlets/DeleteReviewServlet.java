package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling the deletion of a review.
 */
public class DeleteReviewServlet extends HttpServlet {

    /**
    * Processes GET requests to delete a review and redirects to the reviews page.
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
        String hotelName = dbHandler.deleteReview(reviewId, username);
        if (hotelName != null) {
            response.sendRedirect("/reviews?name=" + hotelName);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
