package server.jettyServer.Servlets;

import org.apache.commons.text.StringEscapeUtils;
import server.jettyServer.DatabaseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.velocity.Template;
import server.jettyServer.ServletUtil;

/**
 * Servlet for handling user registration requests.
 */
public class RegistrationServlet extends HttpServlet {


    /**
    * Handles the GET request to display the registration form.
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        context.put("request", request);
        context.put("actionUrl", request.getServletPath());

        Template template = ve.getTemplate("templates/registration.html");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        PrintWriter out = response.getWriter();
        out.println(writer.toString());
    }

    /**
     * Handles the POST request to store registration data into database.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        String username = request.getParameter("name");
        username = StringEscapeUtils.escapeHtml4(username);
        String password = request.getParameter("pass");
        password = StringEscapeUtils.escapeHtml4(password);
        DatabaseHandler dbHandler = DatabaseHandler.getInstance();
        if (dbHandler.userExists(username)) {
            request.setAttribute("error", "Username already taken, Please use another one");
            doGet(request, response);
            return;
        }
        if (!ServletUtil.isValidPassword(password)) {
            request.setAttribute("error", "Password does not meet requirements (At least 4 characters and includes a special character)");
            doGet(request, response);
            return;
        }
        dbHandler.registerUser(username, password);
        response.sendRedirect("/");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
