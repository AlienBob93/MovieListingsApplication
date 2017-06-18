package com.alien.prashantrao.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.alien.prashantrao.popmovies.utilities.fetchPopularMoviesTask;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity
        implements MovieListAdapter.PopularMovieListOnClickHandler {

    private static final String TAG = "MovieListActivity";

    private RecyclerView mRecyclerView;
    public static MovieListAdapter mMovieListAdapter;
    public ProgressBar loadingIndicator;
    private BottomSheetDialog mBottomSheetDialog;
    private View sheetView;

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

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mMovieListAdapter = new MovieListAdapter(this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        // get the sort order last used by the user
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mSortType = sharedPref.getInt(getString(R.string.sort_movie_list_key), SORT_POPULAR);

        mBottomSheetDialog = new BottomSheetDialog(MovieListActivity.this);
        sheetView = MovieListActivity.this.getLayoutInflater().inflate(R.layout.sort_menu, null);

        /* sort the list based on the user preference and save them to the sharedPreferences
         * file
         */
        sheetView.findViewById(R.id.sort_popular).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortType = SORT_POPULAR;
                SharedPreferences sharedPreferences = MovieListActivity.this.getPreferences(Context.MODE_PRIVATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    v.setBackgroundColor(getColor(R.color.colorAccent));
                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                sharedPreferences.edit().putInt(getString(R.string.sort_movie_list_key), mSortType).apply();
                loadMovieData(mSortType);
            }
        });

        sheetView.findViewById(R.id.sort_top_rated).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortType = SORT_TOP_RATED;
                SharedPreferences sharedPreferences = MovieListActivity.this.getPreferences(Context.MODE_PRIVATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    v.setBackgroundColor(getColor(R.color.colorAccent));
                } else {
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                sharedPreferences.edit().putInt(getString(R.string.sort_movie_list_key), mSortType).apply();
                loadMovieData(mSortType);
            }
        });

        sheetView.findViewById(R.id.sort_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFavoritesData();
            }
        });

        mBottomSheetDialog.setContentView(sheetView);

        loadMovieData(mSortType);
    }

    // start the asyncTask to fetch the appropriately sorted movies list
    private void loadMovieData(int args) {
        showLoadingIndicator();
        new fetchPopularMoviesTask(this).execute(args);
        hideLoadingIndicator();
    }

    // launch the favorite movies list
    private void loadFavoritesData() {
        Intent startFavoritesActivity = new Intent(this, FavoritesActivity.class);
        startActivity(startFavoritesActivity);
    }

    private void showSortMenu() {
        mBottomSheetDialog.show();
    }

    public void showLoadingIndicator() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideLoadingIndicator() {
        loadingIndicator.setVisibility(View.GONE);
    }

    public static void setMovieListAdapter(ArrayList<MovieItem> movieItems) {
        mMovieListAdapter.setMovies(movieItems);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:
                // Refresh the list
                loadMovieData(mSortType);
                break;
            case R.id.sort_by:
                showSortMenu();
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
