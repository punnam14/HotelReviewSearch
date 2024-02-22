package hotelapp;

import hotelapp.byhotel.HotelManager;

import hotelapp.byreview.ReviewManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * The AppInitializer class initializes various components for the hotel application.
 * It takes care of initializing threads, reading and processing data, and managing the user interface.
 */
public class AppInitializer {

    /**
     * runApp processes command line arguments and runs the application.
     * Populates the internal data structures as soon as application is run
     * based on the parsed and processed command-line arguments.
     *
     * @param args Command line arguments
     */

    public void runApp(String[] args){
        ArgumentParser parseArgs = new ArgumentParser();
        parseArgs.parseArgs(args);
        String numThreadsStr = parseArgs.getArgument("numThreads");
        int numThreads = checkThreads(numThreadsStr);
        HotelManager hotelManager = new HotelManager();
        ReviewManager reviewManager = new ReviewManager(numThreads);

        hotelManager.processHotel(parseArgs.getArgument("hotelDirectory"));
        Path p = Paths.get(parseArgs.getArgument("reviewDirectory"));
        try (Stream<Path> stream = Files.walk(Paths.get(p.toString()))) {
            stream.filter(Files::isRegularFile)
                    .forEach(path -> {
                        if (path.toString().contains("reviews")) {
                            reviewManager.processReviews(path.toString());

                        }
                    });
        } catch (IOException e) {
            System.out.println("Can not open directory: ");
        }
        reviewManager.shutdown();

        System.out.println("Server is ready.");
    }

    /**
     * Checks the validity of the thread count passed in command-line arguments.
     * If invalid, defaults to 3.
     *
     * @param threads String representing the number of threads
     * @return Valid number of threads
     */
    public int checkThreads(String threads){
        int numThreads;
        if (threads == null || threads.equalsIgnoreCase("")) {
            numThreads = 3;
        }else{
            try {
                numThreads = Integer.parseInt(threads);
            } catch (NumberFormatException e) {
                numThreads = 3;
                System.out.println("Invalid Threads value, defaulting to 3");
            }
        }
        return numThreads;
    }
}
