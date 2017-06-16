package com.alien.prashantrao.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.alien.prashantrao.popmovies.utilities.JsonUtils;
import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.alien.prashantrao.popmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity implements MovieListAdapter.PopularMovieListOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieListAdapter mMovieListAdapter;
    private ProgressBar loadingIndicator;

    public static final int SORT_POPULAR = 1;
    public static final int SORT_TOP_RATED = 2;

    private int mSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load the views
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_popular_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, this.getResources().getInteger(R.integer.spanCount));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        // get the sort order last used by the user
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mSortType = sharedPref.getInt(getString(R.string.sort_movie_list_key), SORT_POPULAR);

        loadMovieData(mSortType);
    }

    // start the asyncTask to fetch the appropriately sorted movies list
    private void loadMovieData(int args) {
        new fetchPopularMoviesTask().execute(args);
    }

    // Background task to fetch the movies from the web API
    private class fetchPopularMoviesTask extends AsyncTask<Integer, Void, ArrayList<MovieItem>> {

        @Override
        protected void onPreExecute() {
            // show the loading indicator while the list is being fetched
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieItem> doInBackground(Integer... params) {

            // query the URL and parse the returned JSON object
            URL requestPopularMoviesUrl = NetworkUtils.buildUrlForJson(params[0]);

            try {
                String JsonResponse = NetworkUtils.getResponseFromHttpUrl(requestPopularMoviesUrl);
                ArrayList<MovieItem> movieListJsonData = JsonUtils
                        .getPopularMoviesListFromJson(MovieListActivity.this, JsonResponse);

                return movieListJsonData;
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
                loadingIndicator.setVisibility(View.INVISIBLE);
                mMovieListAdapter.setMovies(movieItems);
            }
        }
    }

    // force landscape layout on orientation change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, this.getResources().getInteger(R.integer.spanCount)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // change the sort by menu item text according to the current movie list sorting
        MenuItem item = menu.findItem(R.id.sort_popular_top_rated);
        if (mSortType == SORT_POPULAR) {
            item.setTitle(getString(R.string.menu_title_sort_by_ratings));
        } else {
            item.setTitle(getString(R.string.menu_title_sort_by_popularity));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        switch (id) {
            case R.id.refresh:
                // Refresh the list
                loadMovieData(mSortType);
                break;
            case R.id.sort_popular_top_rated:
                /* sort the list based on the user preference and save them to the sharedPreferences
                   file
                 */
                if (mSortType == SORT_POPULAR) {
                    mSortType = SORT_TOP_RATED;
                    sharedPref.edit().putInt(getString(R.string.sort_movie_list_key), mSortType).apply();
                    loadMovieData(mSortType);
                } else {
                    mSortType = SORT_POPULAR;
                    sharedPref.edit().putInt(getString(R.string.sort_movie_list_key), mSortType).apply();
                    loadMovieData(mSortType);
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(MovieItem movieItem) {
        /* Start the activity to display movie details
         *  passing it the selected recyclerView item
         */
        Intent startMovieDetailActivity = new Intent(this, MovieDetailsActivity.class);
        startMovieDetailActivity.putExtra(getString(R.string.intent_key_movie_details), movieItem);
        startActivity(startMovieDetailActivity);
    }
}
