package com.alien.prashantrao.popmovies.utilities;

import android.net.Uri;

import com.alien.prashantrao.popmovies.BuildConfig;
import com.alien.prashantrao.popmovies.MovieListActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Prashant Rao on 12-Jun-17.
 */

public class NetworkUtils {

    private static final String MOVIE_DB_DISCOVER_POPULAR_MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";

    private static final String PATH_POPULAR_MOVIES = "popular";
    private static final String PATH_TOP_RATED_MOVIES = "top_rated";

    private static final String QUERY_API_KEY = "api_key";

    private static final String PATH_TRAILERS = "videos";
    private static final String PATH_REVIEWS = "reviews";

    /**
     * API keys for TheMovieDB
     */
    private static final String API_KEY_V3_AUTH = BuildConfig.THEMOVIE_DB_API_KEY_V3_AUTH;

    // generate appropriate URLs to query Movie listing
    public static URL buildUrlForJson(int SORT_BY_ORDER) {

        switch (SORT_BY_ORDER) {
            case MovieListActivity.SORT_POPULAR:
                Uri builtUriPopular = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(PATH_POPULAR_MOVIES)
                        .appendQueryParameter(QUERY_API_KEY, API_KEY_V3_AUTH)
                        .build();

                URL urlPopular = null;
                try {
                    urlPopular = new URL(builtUriPopular.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                return urlPopular;

            case MovieListActivity.SORT_TOP_RATED:
                Uri builtUriTopRated = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(PATH_TOP_RATED_MOVIES)
                        .appendQueryParameter(QUERY_API_KEY, API_KEY_V3_AUTH)
                        .build();

                URL urlTopRated = null;
                try {
                    urlTopRated = new URL(builtUriTopRated.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                return urlTopRated;

            default:
                return null;
        }
    }

    // generate URL to query movie trailers
    public static URL buildUrlForTrailers(long MOVIE_ID) {
        Uri builtTrailerUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(Long.toString(MOVIE_ID))
                .appendPath(PATH_TRAILERS)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3_AUTH)
                .build();

        URL trailerUrl = null;
        try {
            trailerUrl = new URL(builtTrailerUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return trailerUrl;
    }

    // generate URL to query movie reviews
    public static URL buildUrlForReviews(long MOVIE_ID) {
        Uri builtReviewsUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(Long.toString(MOVIE_ID))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3_AUTH)
                .build();

        URL reviewsUrl = null;
        try {
            reviewsUrl = new URL(builtReviewsUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return reviewsUrl;
    }

    // connect to the web API and fetch the returned data
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
