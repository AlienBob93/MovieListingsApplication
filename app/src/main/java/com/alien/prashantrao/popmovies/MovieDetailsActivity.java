package com.alien.prashantrao.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alien.prashantrao.popmovies.databinding.ActivityMovieDetailsBinding;
import com.alien.prashantrao.popmovies.utilities.JsonUtils;
import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.alien.prashantrao.popmovies.utilities.NetworkUtils;
import com.alien.prashantrao.popmovies.customViews.TrailersAndReviewsBottomSheetFragment;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import static com.alien.prashantrao.popmovies.Data.MovieContract.MovieEntry;
import static com.alien.prashantrao.popmovies.R.string.rating_string_out_of;
import static java.lang.String.valueOf;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MovieDetailsActivity";

    private ActivityMovieDetailsBinding mBinding;
    private MovieItem movieItem;
    private ArrayList<URL> trailerUrls, reviewUrls;

    private boolean isInDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // set content view using DataBindingUtil
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        // Find the toolbar view inside the activity layout
        this.setSupportActionBar(mBinding.movieDetailsActionBar.toolbar);

        // set the actionBar back button
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intentThatStartedThisActivity = getIntent();

        // get movie data from MovieListActivity
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.intent_key_movie_details))) {
                movieItem = intentThatStartedThisActivity.getParcelableExtra(getString(R.string.intent_key_movie_details));

                // set the views
                Picasso.with(this).load(getString(R.string.movie_poster_342px_base_url)
                        + movieItem.getPosterPath()).resize(500, 743)
                        .placeholder(R.drawable.ic_local_movies_black_48dp).into(mBinding.movieDetailsPosterTitle.ivDetailsScreenMoviePoster);

                mBinding.movieDetailsPosterTitle.tvDetailsScreenMovieTitle.setText(movieItem.getTitle());
                mBinding.movieDetailsPosterTitle.tvMovieDetailsRating.setText(valueOf(movieItem.getRatings()) + getString(rating_string_out_of));
                mBinding.movieDetailsPosterTitle.tvMovieReleaseDate.setText(movieItem.getReleaseDate());
                mBinding.movieDetailsPosterTitle.tvMovieDetailsRatingsCount.setText("Average of " + movieItem.getVoteCount() + " ratings");
                mBinding.tvDetailsScreenDescription.tvDescription.setText(movieItem.getDescription());

                // load all the trailers and reviews
                loadTrailers(movieItem.getMovieId());
                loadReviews(movieItem.getMovieId());

                // check if movie is already in favorite
                isInDb = checkIfInFavorites(movieItem);
                Log.i(TAG, "Is in favorites: " + Boolean.toString(isInDb));
            }
        }

        // set the onClickListeners
        mBinding.trailerReviewButtons.btWatchTrailers.setOnClickListener(this);
        mBinding.trailerReviewButtons.btReadReviews.setOnClickListener(this);
    }

    private void loadTrailers(long movieId) {
        new fetchTrailers().execute(movieId);
    }

    private void loadReviews(long movieId) {
        new fetchReviews().execute(movieId);
    }

    private boolean checkIfInFavorites(MovieItem item) {
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, null,
                MovieEntry.COLUMN_MOVIE_ID + " = " + movieItem.getMovieId(),
                null, null);
        if (null != cursor && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            if (null != cursor)
                cursor.close();
            return false;
        }
    }

    private void addFavorite(MovieItem item) {
        // prevent duplicate entries
        if (!isInDb) {
            //insert new movie data via a ContentResolver
            ContentValues contentValues = new ContentValues();
            // put the movie data into the ContentValues
            contentValues.put(MovieEntry.COLUMN_MOVIE_ID, item.getMovieId());
            contentValues.put(MovieEntry.COLUMN_TITLE, item.getTitle());
            contentValues.put(MovieEntry.COLUMN_DESCRIPTION, item.getDescription());
            contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, item.getReleaseDate());
            contentValues.put(MovieEntry.COLUMN_POSTER, item.getPosterPath());
            contentValues.put(MovieEntry.COLUMN_VOTE_AVG, item.getRatings());
            contentValues.put(MovieEntry.COLUMN_VOTE_COUNT, item.getVoteCount());

            // insert the content values via a ContentResolver
            Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
            //Log.v(TAG, uri.toString());

            // show Toast for confirmation
            Toast.makeText(this, R.string.string_added_to_favorites, Toast.LENGTH_SHORT).show();
            isInDb = true;
        }
    }

    private void removeFavorite(MovieItem item) {
        if (isInDb) {
            // get row id by querying the contentResolver
            Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI, null,
                    MovieEntry.COLUMN_MOVIE_ID + " = " + movieItem.getMovieId(),
                    null, null);
            if (null != cursor) {
                // move the cursor to the correct position
                cursor.moveToNext();

                // Build appropriate uri with String row id appended
                Uri uri = MovieEntry.CONTENT_URI;
                uri = uri.buildUpon().
                        appendPath(Integer.toString(cursor.getInt(cursor.getColumnIndex(MovieEntry._ID))))
                        .build();

                Log.v(TAG, "uri for delete: " + uri.toString());
                Log.v(TAG, "removing from id: " +
                        cursor.getInt(cursor.getColumnIndex(MovieEntry._ID)));
                // delete the movie from favorites
                getContentResolver().delete(uri, null, null);

                // show Toast for confirmation
                Toast.makeText(this, R.string.string_removed_from_favorites, Toast.LENGTH_SHORT).show();
                isInDb = false;
                // close the cursor
                cursor.close();
            }
        }
    }

    private void callBottomSheetFragment(final ArrayList<URL> urls, String generalTextForListItems) {
        // create the Bottom Sheet dialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.trailers_reviews_bottom_sheet);
        TrailersAndReviewsBottomSheetFragment bottomSheetFragment =
                (TrailersAndReviewsBottomSheetFragment) dialog.findViewById(R.id.trailers_reviews_sheet);

        // set the list text
        ArrayList<String> numberedUrlArray = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            numberedUrlArray.add(generalTextForListItems + Integer.toString(i+1));
        }

        // set the adapter for the bottomSheet
        assert bottomSheetFragment != null;
        bottomSheetFragment.setAdapter(new ArrayAdapter<>(MovieDetailsActivity.this,
                android.R.layout.simple_list_item_1, numberedUrlArray.toArray()));

        // set the on click method for the list items
        bottomSheetFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(position).toString()));
                startActivity(browserIntent);
            }
        });

        // show the bottom sheet
        dialog.show();
        numberedUrlArray.clear();
    }

    private class fetchTrailers extends AsyncTask<Long, Void, ArrayList<URL>> {
        @Override
        protected ArrayList<URL> doInBackground(Long... params) {
            URL buildUrlForTrailersJson = NetworkUtils.buildUrlForTrailers(params[0]);

            try {
                String JsonResponseTrailers = NetworkUtils.getResponseFromHttpUrl(buildUrlForTrailersJson);
                return  JsonUtils.getMovieTrailers(MovieDetailsActivity.this, JsonResponseTrailers);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<URL> urls) {
            if (null != urls)
                trailerUrls = urls;
        }
    }

    private class fetchReviews extends AsyncTask<Long, Void, ArrayList<URL>> {
        @Override
        protected ArrayList<URL> doInBackground(Long... params) {
            URL buildUrlForReviewsJson = NetworkUtils.buildUrlForReviews(params[0]);

            try {
                String JsonResponseReviews = NetworkUtils.getResponseFromHttpUrl(buildUrlForReviewsJson);
                return JsonUtils.getMovieReviews(MovieDetailsActivity.this, JsonResponseReviews);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<URL> urls) {
            if (null != urls) {
                reviewUrls = urls;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.bt_watch_trailers:
                if (null != trailerUrls) {
                    callBottomSheetFragment(trailerUrls, "Watch trailer ");
                } else {
                    Toast.makeText(this, "There seems to be something wrong. :(", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_read_reviews:
                if (null != reviewUrls) {
                    callBottomSheetFragment(reviewUrls, "Read review ");
                } else {
                    Toast.makeText(this, "No reviews found.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_screen_add_favorite, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.add_remove_favorite);

        // change the fav icon based on if the movie is a favorite
        if (isInDb) {
            item.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // set the correct actionBar back button properties
            case android.R.id.home:
                onBackPressed();
                return true;
            // set the favorite icon and action to reflect the context
            case R.id.add_remove_favorite:
                if (!isInDb) {
                    addFavorite(movieItem);
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                } else {
                    removeFavorite(movieItem);
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
