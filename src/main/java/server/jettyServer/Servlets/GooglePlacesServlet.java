package server.jettyServer.Servlets;

import server.jettyServer.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class GooglePlacesServlet extends HttpServlet {

    /**
     * Handles the GET request to display nearby Restaurants.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletUtil.checkLoggedIn(request, response)) {
            return;
        }
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");
        String apiKey = "AIzaSyCxnP4-Gb0a98ZVhKxVijrDHEvE3ZmoiwQ";
        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + latitude + "," + longitude + "&radius=8000&type=restaurant&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        InputStream inputStream;
        if (status > 299) {
            inputStream = connection.getErrorStream();
        } else {
            inputStream = connection.getInputStream();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            content.append(inputLine);
        }
        reader.close();
        connection.disconnect();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(content.toString());
        out.flush();
    }
}