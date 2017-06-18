package com.alien.prashantrao.popmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alien.prashantrao.popmovies.utilities.MovieItem;

import static com.alien.prashantrao.popmovies.Data.MovieContract.MovieEntry;

public class FavoritesActivity extends AppCompatActivity
        implements FavoritesListAdapter.FavoritesListAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "FavoritesActivity";

    private static final int ID_FAVORITES_LOADER = 666;

    private FavoritesListAdapter mFavoritesListAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the actionBar back button
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // set the recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_favorites_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, this.getResources().getInteger(R.integer.spanCount));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mFavoritesListAdapter = new FavoritesListAdapter(this, this);
        mRecyclerView.setAdapter(mFavoritesListAdapter);

         /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
         getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);
    }

    private void showFavoritesView() {

    }

    // force landscape layout on orientation change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, this.getResources().getInteger(R.integer.spanCount)));
    }

    // get all the movies stored in the DataBase
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_FAVORITES_LOADER:
                Uri favMoviesQueryUri = MovieEntry.CONTENT_URI;

                return new CursorLoader(this,
                        favMoviesQueryUri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // call mFavoritesListAdapter's swapCursor method and pass in the new Cursor
        mFavoritesListAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) {
            showFavoritesView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
          /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mFavoritesListAdapter.swapCursor(null);
    }

    @Override
    public void onClick(MovieItem movieItem) {
       /* Start the activity to display movie details
        * passing it the selected recyclerView item
        */
        Intent startMovieDetailActivity = new Intent(this, MovieDetailsActivity.class);
        startMovieDetailActivity.putExtra(getString(R.string.intent_key_movie_details), movieItem);
        startActivity(startMovieDetailActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // set the correct actionBar back button properties
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
