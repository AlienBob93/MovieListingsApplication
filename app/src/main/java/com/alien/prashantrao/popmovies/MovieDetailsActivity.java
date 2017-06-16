package com.alien.prashantrao.popmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alien.prashantrao.popmovies.utilities.JsonUtils;
import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.alien.prashantrao.popmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import static com.alien.prashantrao.popmovies.R.string.rating_string_out_of;
import static java.lang.String.valueOf;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private MovieItem movieItem;
    private ArrayList<URL> trailerUrls, reviewUrls;

    private ImageView moviePoster;
    private TextView movieTitle, movieRating, movieReleaseDate, movieVoteCount, movieDescription;
    private Button watchTrailer, readReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set the actionBar back button
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // TODO (1) add fav button

        moviePoster = (ImageView) findViewById(R.id.iv_details_screen_movie_poster);
        movieTitle = (TextView) findViewById(R.id.tv_details_screen_movie_title);
        movieRating = (TextView) findViewById(R.id.tv_movie_details_rating);
        movieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        movieVoteCount = (TextView) findViewById(R.id.tv_movie_details_ratings_count);
        movieDescription = (TextView) findViewById(R.id.tv_details_screen_description);
        //watchTrailer = (Button) findViewById(R.id.bt_watch_trailer);
        //readReviews = (Button) findViewById(R.id.bt_read_reviews);

        Intent intentThatStartedThisActivity = getIntent();

        // get movie data from MovieListActivity
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(getString(R.string.intent_key_movie_details))) {
                movieItem = intentThatStartedThisActivity.getParcelableExtra(getString(R.string.intent_key_movie_details));

                // set the views
                Picasso.with(this).load(getString(R.string.movie_poster_500px_base_url)
                        + movieItem.getPosterPath()).into(moviePoster);

                movieTitle.setText(movieItem.getTitle());
                movieRating.setText(valueOf(movieItem.getRatings()) + getString(rating_string_out_of));
                movieReleaseDate.setText(movieItem.getReleaseDate());
                movieVoteCount.setText("Average of " + movieItem.getVoteCount() + " ratings");
                movieDescription.setText(movieItem.getDescription());
                loadTrailers(movieItem.getMovieId());
                loadReviews(movieItem.getMovieId());

            }
        }

        //watchTrailer.setOnClickListener(this);
        //readReviews.setOnClickListener(this);
    }

    private void loadTrailers(long movieId) {
        new fetchTrailers().execute(movieId);
    }

    private void loadReviews(long movieId) {
        new fetchReviews().execute(movieId);
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

        /*switch (id) {
            case R.id.bt_watch_trailer:
                if (null != trailerUrls) {
                    Intent streamVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrls.get(0).toString()));
                    startActivity(streamVideoIntent);
                } else {
                    Toast.makeText(this, "There seems to be something wrong. :(", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_read_reviews:
                if (null != reviewUrls) {
                    Intent streamVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrls.get(0).toString()));
                    startActivity(streamVideoIntent);
                } else {
                    Toast.makeText(this, "No reviews found.", Toast.LENGTH_SHORT).show();
                }
                break;
        }*/
    }

    // set the correct actionBar back button properties
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
