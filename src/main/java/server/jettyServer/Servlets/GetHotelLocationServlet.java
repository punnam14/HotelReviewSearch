package server.jettyServer.Servlets;

import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class GetHotelLocationServlet extends HttpServlet {

    /**
     * Handles the GET request to get latitude and longitude.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        String hotelId = request.getParameter("hotelId");
        response.setContentType("application/json");
        try {
            String hotelName  = DatabaseHandler.getInstance().getHotelName(hotelId);
            Map<String,String> hotelInfo = DatabaseHandler.getInstance().getHotelInfo(hotelName);
            if (hotelInfo != null) {
                String lat = hotelInfo.get("latitude");
                String lon = hotelInfo.get("longitude");
                String jsonResponse = String.format(
                        "{\"latitude\": %s, \"longitude\": %s}",
                        lat, lon);
                response.getWriter().write(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Hotel not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
}