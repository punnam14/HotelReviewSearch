package server;

import hotelapp.AppInitializer;
import server.jettyServer.DatabaseHandler;
import server.jettyServer.JettyServer;

public class JettyHotelServer {
	public static final int PORT = 9000;

	public static void main(String[] args)  {
		DatabaseHandler dbHandler = DatabaseHandler.getInstance();
		Boolean tables = dbHandler.createTable();
		if(!tables){
			AppInitializer app = new AppInitializer();
			app.runApp(args);
		}
		JettyServer jettyServer = new JettyServer(PORT);
		jettyServer.addMapping("/getPlaces","server.jettyServer.Servlets.GooglePlacesServlet");
		jettyServer.addMapping("/getHotelLocation","server.jettyServer.Servlets.GetHotelLocationServlet");
		jettyServer.addMapping("/clearExpediaVisits","server.jettyServer.Servlets.ClearVisitsServlet");
		jettyServer.addMapping("/recordVisit","server.jettyServer.Servlets.RecordVisitServlet");
		jettyServer.addMapping("/clearFavorites","server.jettyServer.Servlets.ClearFavoritesServlet");
		jettyServer.addMapping("/removeFavorite", "server.jettyServer.Servlets.RemoveFavoriteServlet");
		jettyServer.addMapping("/addFavorite", "server.jettyServer.Servlets.FavoriteServlet");
		jettyServer.addMapping("/hotels", "server.jettyServer.Servlets.HotelServlet");
		jettyServer.addMapping("/reviews", "server.jettyServer.Servlets.ReviewServlet");
		jettyServer.addMapping("/submitReview", "server.jettyServer.Servlets.SubmitReviewServlet");
		jettyServer.addMapping("/editReview", "server.jettyServer.Servlets.EditReviewServlet");
		jettyServer.addMapping("/deleteReview", "server.jettyServer.Servlets.DeleteReviewServlet");
		jettyServer.addMapping("/register", "server.jettyServer.Servlets.RegistrationServlet");
		jettyServer.addMapping("/login", "server.jettyServer.Servlets.LoginServlet");
		jettyServer.addMapping("/logout", "server.jettyServer.Servlets.LogoutServlet");
		jettyServer.addMapping("/", "server.jettyServer.Servlets.LandingPageServlet");
		try {
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}