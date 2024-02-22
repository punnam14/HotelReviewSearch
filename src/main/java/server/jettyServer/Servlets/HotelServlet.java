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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


/**
 * Servlet for handling hotel-related requests.
 * This servlet interacts with ThreadSafeHotelFinder to fetch detailed information about a specific hotel.
 */
public class HotelServlet extends HttpServlet {

    /**
     * Handles the GET request by fetching hotel information for a specified hotel ID.
     * Extracts the hotel ID from the request parameters, retrieves hotel data, and sends it back in the response.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        PrintWriter out = response.getWriter();
        String searchTerm = request.getParameter("searchTerm");
        List<String> hotelNames;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            hotelNames = DatabaseHandler.getInstance().getFilteredHotelNames(searchTerm);
        } else {
            hotelNames = DatabaseHandler.getInstance().getAllHotelNames();
        }
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("hotelNames", hotelNames);
        Template template = ve.getTemplate("templates/hotels.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
