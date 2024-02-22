package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Servlet for handling logout requests.
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Handles the GET request to log out a user.
     * Invalidates the user's session and redirects to the homepage.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Timestamp loginTime = (Timestamp) session.getAttribute("currentLoginTime");
            if (loginTime != null) {
                DatabaseHandler dbHandler = DatabaseHandler.getInstance();
                String loggedInUsername = (String) session.getAttribute("username");
                dbHandler.storeLastLogin(loggedInUsername, loginTime);
            }
            session.invalidate();
        }
        response.sendRedirect("/");
    }
}
