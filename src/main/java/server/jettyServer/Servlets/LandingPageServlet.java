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
import java.util.HashMap;
import java.util.List;

/**
 * Servlet for handling requests to the landing page.
 */
public class LandingPageServlet extends HttpServlet {

    /**
     * Handles the GET request to display the landing page.
     * Sets up the necessary context for the Velocity template and renders it.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        String loggedInUsername = (String) session.getAttribute("username");
        String lastLoginMessage = (String) session.getAttribute("lastLoginMessage");

        List<String> hotelNames = DatabaseHandler.getInstance().GetFavoriteHotelsOfUser(loggedInUsername);
        HashMap<String,String> visits = DatabaseHandler.getInstance().getLinkVisitsOfUser(loggedInUsername);

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("lastLoginMessage",lastLoginMessage);
        context.put("favoriteHotels", hotelNames);
        context.put("username",loggedInUsername);
        context.put("expediaVisits", visits);

        Template template = ve.getTemplate("templates/landingPage.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        HttpSession session = request.getSession();
        String loggedInUsername = (String) session.getAttribute("username");
        DatabaseHandler.getInstance().clearFavorites(loggedInUsername);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("All favorites cleared");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
