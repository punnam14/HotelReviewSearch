package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RemoveFavoriteServlet extends HttpServlet {

    /**
     * Handles the POST request to remove favorite hotels.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("username");
        String hotelName = request.getParameter("hotelName");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        dbHandler.removeFavorite(user, hotelName);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Hotel removed from favorites");
    }
}