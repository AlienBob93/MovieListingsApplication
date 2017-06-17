package com.alien.prashantrao.popmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by prash on 16-Jun-17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.alien.prashantrao.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorite movies" directory
    public static final String PATH_FAV_MOVIES = "favorite_movies";

    public static final class MovieEntry implements BaseColumns {
        // Table name
        public static final String FAV_MOVIE_TABLE_NAME = "favorite_movies";

        // columns
        public static final String _ID = "_id";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_VOTE_AVG = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        /*
         * create content uri
         * content URI = base content URI + path
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAV_MOVIES).build();

    }
}
