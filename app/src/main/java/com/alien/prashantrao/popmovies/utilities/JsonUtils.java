package com.alien.prashantrao.popmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*
 * Created by Prashant Rao on 12-Jun-17.
 */

public class JsonUtils {

    // parse the movie listing JSON object
    public static ArrayList<MovieItem> getPopularMoviesListFromJson(Context context, String movieListJsonStr)
            throws JSONException {

        /*
          json KEYS
         */
        final String OWM_RESULTS = "results";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String ORG_LANGUAGE = "original_language";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVG = "vote_average";
        final String VOTE_COUNT = "vote_count";
        final String MOVIE_ID = "id";

        // status message keys
        final String STATUS_CODE = "status_code";
        final String OWM_MESSAGE_CODE = "cod";

        ArrayList<MovieItem> parsedMovieData = new ArrayList<>();

        JSONObject movieListJsonObj = new JSONObject(movieListJsonStr);

        /*
          check for errors.
         */
        if (movieListJsonObj.has(STATUS_CODE)) {
            int statusCode = movieListJsonObj.getInt(STATUS_CODE);
            if (statusCode > 0) {
                return null;
            }
        }
        if (movieListJsonObj.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieListJsonObj.getInt(OWM_MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        /*
          The movie list is stored inside the "results" array
         */
        JSONArray movieResultsArray = movieListJsonObj.getJSONArray(OWM_RESULTS);

        for (int i = 0; i < movieResultsArray.length(); i++) {
            String title;
            String originalLanguage;
            String movieOverview;
            String releaseDate;
            String posterPath;

            long voteCount;
            int voteAvg;
            long movieId;

            JSONObject movie = movieResultsArray.getJSONObject(i);

            title = movie.getString(TITLE);
            posterPath = movie.getString(POSTER_PATH);
            originalLanguage = movie.getString(ORG_LANGUAGE);
            movieOverview = movie.getString(OVERVIEW);
            releaseDate = movie.getString(RELEASE_DATE);

            voteAvg = movie.getInt(VOTE_AVG);
            voteCount = movie.getLong(VOTE_COUNT);
            movieId = movie.getLong(MOVIE_ID);

            MovieItem movieItem = new MovieItem(title, originalLanguage, movieOverview,
                    releaseDate, posterPath, voteCount, voteAvg, movieId);
            parsedMovieData.add(i, movieItem);
        }

        return parsedMovieData;
    }

    // parse the movie trailers JSON object
    public static ArrayList<URL> getMovieTrailers(Context context, String movieTrailersJsonStr) throws JSONException {

        final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
        final String QUERY_WATCH = "v";
        final String OWM_RESULTS = "results";
        final String VIDEO_KEY = "key";

        // status message keys
        final String STATUS_CODE = "status_code";
        final String OWM_MESSAGE_CODE = "cod";

        JSONObject movieTrailersJsonObj = new JSONObject(movieTrailersJsonStr);

        /*
          check for errors.
         */
        if (movieTrailersJsonObj.has(STATUS_CODE)) {
            int statusCode = movieTrailersJsonObj.getInt(STATUS_CODE);
            if (statusCode > 0) {
                return null;
            }
        }
        if (movieTrailersJsonObj.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieTrailersJsonObj.getInt(OWM_MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieTrailersArray = movieTrailersJsonObj.getJSONArray(OWM_RESULTS);
        ArrayList<URL> trailerUrls = new ArrayList<>();

        // get all the trailers
        for (int i = 0; i < movieTrailersArray.length(); i++) {
            String trailerId;

            JSONObject trailer = movieTrailersArray.getJSONObject(i);
            trailerId = trailer.getString(VIDEO_KEY);

            Uri trailerUri = Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                    .appendQueryParameter(QUERY_WATCH, trailerId)
                    .build();

            URL trailerUrl = null;
            try {
                trailerUrl = new URL(trailerUri.toString());
                trailerUrls.add(trailerUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return trailerUrls;
    }

    // parse the movie reviews JSON object
    public static ArrayList<URL> getMovieReviews(Context context, String movieReviewsJsonStr) throws JSONException {

        final String OWM_RESULTS = "results";
        final String REVIEW_URL = "url";

        // status message keys
        final String STATUS_CODE = "status_code";
        final String OWM_MESSAGE_CODE = "cod";

        JSONObject movieReviewsJsonObj = new JSONObject(movieReviewsJsonStr);

        /*
          check for errors.
         */
        if (movieReviewsJsonObj.has(STATUS_CODE)) {
            int statusCode = movieReviewsJsonObj.getInt(STATUS_CODE);
            if (statusCode > 0) {
                return null;
            }
        }
        if (movieReviewsJsonObj.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieReviewsJsonObj.getInt(OWM_MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieReviewsArray = movieReviewsJsonObj.getJSONArray(OWM_RESULTS);
        ArrayList<URL> reviewUrls = new ArrayList<>();

        // get all the reviews
        for (int i = 0; i < movieReviewsArray.length(); i++) {
            String reviewUrlStr;

            JSONObject review = movieReviewsArray.getJSONObject(i);
            reviewUrlStr = review.getString(REVIEW_URL);
            Log.v("TAG", reviewUrlStr);

            URL reviewUrl = null;
            try {
                reviewUrl = new URL(reviewUrlStr);
                reviewUrls.add(reviewUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return reviewUrls;
    }
}
