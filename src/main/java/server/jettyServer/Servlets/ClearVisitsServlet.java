package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ClearVisitsServlet extends HttpServlet {

    /**
     * Handles the POST request to clear user expedia links visited.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("username");
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();

        dbHandler.clearExpediaLinks(user);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Links Cleared.");
    }
}
