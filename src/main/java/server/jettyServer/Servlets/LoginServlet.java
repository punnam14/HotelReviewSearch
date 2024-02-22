package server.jettyServer.Servlets;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.jettyServer.DatabaseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;

/**
 * Servlet for handling login requests.
 */
public class LoginServlet extends HttpServlet {
    /**
     * Handles the GET request to display the login page.
     * Redirects to the homepage if the user is already logged in.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();
        context.put("actionUrl", request.getServletPath());
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect("/");
        } else {
            Template template = ve.getTemplate("templates/login.html");
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            PrintWriter out = response.getWriter();
            out.println(writer.toString());
        }
    }



    /**
     * Handles the POST request for user authentication.
     * Processes the login credentials, authenticates the user, and manages the session.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("pass");
        username = StringEscapeUtils.escapeHtml4(username);
        password = StringEscapeUtils.escapeHtml4(password);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        boolean authenticated = dbHandler.authenticateUser(username, password);
        if (authenticated) {
            HttpSession session = request.getSession(true);
            session.setAttribute("username", username);
            response.sendRedirect("/");
            Timestamp lastLogin = dbHandler.getLastLogin(username);
            String lastLoginMessage = "You have not logged in before";
            if (lastLogin != null) {
                lastLoginMessage = "Last Login: " + lastLogin;
            }
            session.setAttribute("lastLoginMessage",lastLoginMessage);
            session.setAttribute("currentLoginTime", new Timestamp(System.currentTimeMillis()));
        } else {
            VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
            VelocityContext context = new VelocityContext();
            context.put("errorMessage", "Invalid username or password.");
            context.put("actionUrl", request.getServletPath());
            Template template = ve.getTemplate("templates/login.html");
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            PrintWriter out = response.getWriter();
            out.println(writer.toString());
        }
    }
}
