package server.jettyServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ServletUtil {
    /**
     * Checks if the user is logged in (i.e., if the session exists and contains a username).
     * If the user is not logged in, it redirects to the login page.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @return true if the user is logged in, false otherwise.
     * @throws IOException if an I/O error occurs in redirecting.
     */
    public static boolean checkLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided password is valid based on specific criteria.
     * This method evaluates the password's validity by ensuring it meets two conditions:
     * 1. The password length must be at least 4 characters.
     * 2. The password must contain at least one special character from the set [!@#$%^&*].
     *
     * @param password The password string to be evaluated for validity.
     * @return true if the password meets the specified criteria; false otherwise.
     */
    public static boolean isValidPassword(String password) {
        if (password.length() < 4) {
            return false;
        }
        if (!password.matches(".*[!@#$%^&*].*")) {
            return false;
        }
        return true;
    }

}
