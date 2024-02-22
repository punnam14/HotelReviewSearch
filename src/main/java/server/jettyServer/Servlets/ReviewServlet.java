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
import java.util.List;
import java.util.Map;

/**
 * Servlet for handling review-related requests.
 */
public class ReviewServlet extends HttpServlet {

    /**
     * Handles the GET request to retrieve and display reviews.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        String hotelNameForUrl;
        String cityForUrl;
        String hotelIdForUrl;
        String expediaUrl = "";
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        String loggedInUsername = (String) session.getAttribute("username");
        String hotelName = request.getParameter("name");
        Map<String, String> hotelInfo = DatabaseHandler.getInstance().getHotelInfo(hotelName);
        String hotelId = hotelInfo.get("id");
        List<Map<String, String>> reviews = DatabaseHandler.getInstance().getReviewsForHotel(hotelId);
        double avgRating = DatabaseHandler.getInstance().averageRating(hotelId);
        if(hotelInfo.get("name") != null){
            hotelNameForUrl = hotelInfo.get("name").replaceAll(" ", "-");
            cityForUrl = hotelInfo.get("city").replaceAll(" ", "-");
            hotelIdForUrl = hotelInfo.get("id");
            expediaUrl = "https://www.expedia.com/" + cityForUrl + "-Hotels-" + hotelNameForUrl + ".h" + hotelIdForUrl + ".Hotel-Information";
        }
        boolean isFavorited = DatabaseHandler.getInstance().checkFavoriteHotelOfUser(loggedInUsername,hotelName);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("hotel", hotelInfo);
        context.put("reviews", reviews);
        context.put("averageRating", avgRating);
        context.put("expediaUrl", expediaUrl);
        context.put("loggedInUsername", loggedInUsername);
        context.put("isFavorited", isFavorited);
        Template template = ve.getTemplate("templates/reviews.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
