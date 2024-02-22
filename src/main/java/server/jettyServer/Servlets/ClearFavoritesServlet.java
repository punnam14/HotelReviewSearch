package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class ClearFavoritesServlet extends HttpServlet {

    /**
     * Handles the POST request to clear user favorite hotels.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String loggedInUsername = (String) session.getAttribute("username");
        DatabaseHandler.getInstance().clearFavorites(loggedInUsername);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("All favorites cleared");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
