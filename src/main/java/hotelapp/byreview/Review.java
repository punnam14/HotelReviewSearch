package hotelapp.byreview;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Review implements Comparable<Review> {
    private final String reviewId;
    private final String ratingOverall;
    private final String title;
    private final String reviewText;
    private final String userNickname;
    private final String reviewSubmissionTime;

    /**
     * Constructs a new Review object with the given attributes.
     *
//     * @param hotelId            The ID of the hotel the review is for.
     * @param reviewId           The ID of the review.
     * @param ratingOverall      The overall rating of the hotel by the reviewer.
     * @param title              The title of the review.
     * @param reviewText         The text content of the review.
     * @param userNickname       The nickname of the reviewer.
     * @param reviewSubmissionTime The time the review was submitted.
     */
    public Review(String reviewId, String ratingOverall, String title, String reviewText, String userNickname, String reviewSubmissionTime) {
        this.reviewId = reviewId;
        this.ratingOverall = ratingOverall;
        this.title = title;
        this.reviewText = reviewText;
        this.userNickname = userNickname;
        this.reviewSubmissionTime = reviewSubmissionTime;
    }

    /**
     * Retrieves the title of the review.
     *
     * @return The title.
     */
    public String getTitle() {
        return title.trim().isEmpty() ? "No title" : title;
    }

    /**
     * Retrieves the username of the review.
     *
     * @return The username.
     */
    public String getUsername() {
        return userNickname.trim().isEmpty() ? "Anonymous" : userNickname;
    }

    /**
     * Retrieves the rating of the review.
     *
     * @return The review rating.
     */
    public String getRatingOverall() {
        return ratingOverall;
    }

    /**
     * Retrieves the ID of the review.
     *
     * @return The review ID.
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Retrieves the text content of the review.
     *
     * @return The review text.
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Retrieves the submission time of the review.
     *
     * @return The submission time in string format.
     */
    public String getReviewSubmissionTime() {
        return reviewSubmissionTime;
    }

    /**
     * Retrieves the date without time of the review.
     *
     * @return The date in string format.
     */
    public String getDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDate date = LocalDate.parse(reviewSubmissionTime, formatter);
        return date.toString();
    }

    /**
     * Returns a string representation of the review.
     *
     * @return A string describing this review.
     */
    @Override
    public String toString() {
        String displayName = (userNickname == null || userNickname.trim().isEmpty()) ? "Anonymous" : userNickname;
        return "--------------------" + System.lineSeparator() +
                "Review by " + displayName + " on " + getDate() + System.lineSeparator() +
                "Rating: " + ratingOverall + System.lineSeparator() +
                "ReviewId: " + reviewId + System.lineSeparator() +
                title + System.lineSeparator() +
                reviewText + System.lineSeparator();
    }

    /**
     * Compares this review with another based on submission time and review ID.
     *
     * @param o Another Review object to compare.
     * @return Integer value resulting from the comparison.
     */
    @Override
    public int compareTo(Review o) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDate date1 = LocalDate.parse(this.getReviewSubmissionTime(), formatter);
        LocalDate date2 = LocalDate.parse(o.getReviewSubmissionTime(), formatter);
        int dateComparison = date2.compareTo(date1);
        if (dateComparison != 0) {
            return dateComparison;
        } else {
            return this.getReviewId().compareTo(o.getReviewId());
        }
    }
}
