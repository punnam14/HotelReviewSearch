package server.jettyServer;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

/** This class uses Jetty & servlets to implement server responding to Http GET requests */
public class JettyServer {
    private Server jettyServer;
    private ServletContextHandler handler;
    private final Map<String, Object> resources = new HashMap<>();

    /**
     * Constructs a JettyServer. Initializes the Jetty server on the specified port and sets up a context handler.
     */
    public JettyServer(int port) {
        jettyServer = new Server(port);
        handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        jettyServer.setHandler(handler);
    }
    /**
     * Maps a given URL path/endpoint to the name of the servlet class that will handle requests coming at this endpoint
     * @param path end point
     * @param className  name of the servlet class
     */
    public void addMapping(String path, String className){
        try{
            Class<?> handleClass = Class.forName(className);
            Servlet servlet = (Servlet) handleClass.getDeclaredConstructor().newInstance();
            ServletHolder holder = new ServletHolder(servlet);
            handler.addServlet(holder, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a resource to the server context, making it available to servlets.
     *
     * @param name The name associated with the resource.
     * @param resource The object representing the resource.
     */
    public void addResource(String name, Object resource) {
        resources.put(name, resource);
    }


    /**
     * Function that starts the server
     * @throws Exception throws exception if access failed
     */
    public  void start() throws Exception {
        resources.forEach((name, resource) -> handler.setAttribute(name, resource));
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        handler.setAttribute("templateEngine", velocity);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, handler });
        jettyServer.setHandler(handlers);

        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
