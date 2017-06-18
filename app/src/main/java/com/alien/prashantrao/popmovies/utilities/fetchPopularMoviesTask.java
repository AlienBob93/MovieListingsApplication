package com.alien.prashantrao.popmovies.utilities;

import android.content.Context;
import android.os.AsyncTask;

import com.alien.prashantrao.popmovies.MovieListActivity;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by prash on 17-Jun-17.
 */

public class fetchPopularMoviesTask extends AsyncTask<Integer, Void, ArrayList<MovieItem>> {

    // Background task to fetch the movies from the web API

    private Context mContext;
    public fetchPopularMoviesTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        // show the loading indicator while the list is being fetched
    }

    @Override
    protected ArrayList<MovieItem> doInBackground(Integer... params) {

        // query the URL and parse the returned JSON object
        URL requestPopularMoviesUrl = NetworkUtils.buildUrlForJson(params[0]);

        try {
            String JsonResponse = NetworkUtils.getResponseFromHttpUrl(requestPopularMoviesUrl);

            return JsonUtils
                    .getPopularMoviesListFromJson(mContext, JsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            /* if the returned list is not empty (if the retrieval was successful)
             * hide the loading indicator and show the list
             */
        if (movieItems != null) {
            //loadingIndicator.setVisibility(View.INVISIBLE);
            MovieListActivity.setMovieListAdapter(movieItems);
        }
    }
}