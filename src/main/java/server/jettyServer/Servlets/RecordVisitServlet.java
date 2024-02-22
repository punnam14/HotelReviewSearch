package server.jettyServer.Servlets;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.jettyServer.DatabaseHandler;
import server.jettyServer.ServletUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

public class RecordVisitServlet extends HttpServlet {

    /**
     * Handles the POST request to record expedia URL visit.
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
        String expediaUrl = request.getParameter("expediaUrl");
        dbHandler.recordVisit(user, expediaUrl);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Visit Recorded.");
    }
}