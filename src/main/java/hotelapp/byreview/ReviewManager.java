package hotelapp.byreview;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.jettyServer.DatabaseHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * The ReviewManager class manages the processing of multiple review files concurrently.
 * It uses a fixed thread pool and a Phaser for coordination among threads.
 */
public class ReviewManager {
    private final ExecutorService executorService;
    private final Phaser phaser;

    public ReviewManager(int numberOfThreads) {
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.phaser = new Phaser();
    }

    /**
     * Submits each review file path for processing as a new task in the thread pool.
     *
     * @param reviewPath List of file paths to the review JSON files.
     */
    public void processReviews(String reviewPath) {
        ReviewProcessor task = new ReviewProcessor(reviewPath);
        executorService.submit(task);
        phaser.register();
    }

    /**
     * Waits for all tasks to complete and then shuts down the thread pool.
     */
    public void shutdown() {
        phaser.awaitAdvance(phaser.getPhase());
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    /**
     * The ReviewProcessor class represents a single task for processing a review file and database.
     * Implements Runnable interface for execution in a thread pool.
     */
    public class ReviewProcessor implements Runnable{
        private final String filePath;

        public ReviewProcessor(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Parses a review file and merges its reviews into a global thread-safe review storage.
         */
        @Override
        public void run() {
            Gson gson = new Gson();
            try (FileReader fr = new FileReader(filePath)) {
                JsonParser parser = new JsonParser();
                JsonObject jo = (JsonObject) parser.parse(fr);
                JsonObject reviewDetails = jo.getAsJsonObject("reviewDetails");
                JsonArray reviewSummaryArray = reviewDetails.getAsJsonObject("reviewCollection").getAsJsonArray("review");
                String hotelId = reviewSummaryArray.get(0).getAsJsonObject().get("hotelId").getAsString();
                JsonObject reviewCollection = reviewDetails.getAsJsonObject("reviewCollection");
                JsonArray jsonArr = reviewCollection.getAsJsonArray("review");
                Review[] reviewsList = gson.fromJson(jsonArr, Review[].class);
                TreeSet<Review> reviewSet = new TreeSet<>();
                reviewSet.addAll(Arrays.asList(reviewsList));
                DatabaseHandler dbHandler = DatabaseHandler.getInstance();
                for (Review review : reviewSet) {
                    dbHandler.addReview(
                            review.getReviewId(),
                            hotelId,
                            review.getRatingOverall(),
                            review.getTitle(),
                            review.getReviewText(),
                            review.getUsername().strip().trim(),
                            review.getReviewSubmissionTime().toString()
                    );
                dbHandler.registerUser(review.getUsername().strip().trim().toLowerCase(),"Hello@");
                }
            } catch (IOException e) {
                System.out.println("Could not read the file: " + e);
            }finally {
                phaser.arriveAndDeregister();
            }
        }
    }
}
