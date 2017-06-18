package com.alien.prashantrao.popmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.squareup.picasso.Picasso;

import static com.alien.prashantrao.popmovies.Data.MovieContract.MovieEntry;

/**
 * Created by prash on 17-Jun-17.
 */

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.FavoriteMovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    final private FavoritesListAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface FavoritesListAdapterOnClickHandler {
        void onClick(MovieItem movieItem);
    }

    // constructor initializes the context
    public FavoritesListAdapter(Context context, FavoritesListAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new FavoriteMovieViewHolder that holds the view for each task
     */
    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the layout to a view
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new FavoriteMovieViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, int position) {
        // indices for the favorite movies columns _id, title and poster
        int idIndex = mCursor.getColumnIndex(MovieEntry._ID);
        int titleIndex = mCursor.getColumnIndex(MovieEntry.COLUMN_TITLE);
        int posterIndex = mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER);

        // move to the correct position in the cursor
        mCursor.moveToPosition(position);

        // determine values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String title = mCursor.getString(titleIndex);
        String posterPath = mCursor.getString(posterIndex);

        // set values
        holder.itemView.setTag(id);
        holder.movie_title.setText(title);
        // load poster with placeholders
        Picasso.with(mContext)
                .load(mContext.getString(R.string.movie_poster_342px_base_url) + posterPath)
                .placeholder(R.drawable.ic_local_movies_black_48dp).into(holder.movie_poster);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor(Cursor newCursor) {
        // check if this cursor is the same as the previous cursor (mCursor)
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class FavoriteMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView movie_poster;
        private final TextView movie_title;

        public FavoriteMovieViewHolder(View itemView) {
            super(itemView);

            movie_poster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            movie_title = (TextView) itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            MovieItem movieItem = new MovieItem(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_TITLE)), "en",
                    mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_DESCRIPTION)),
                    mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)),
                    mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_POSTER)),
                    mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT)),
                    mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVG)),
                    mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
            mClickHandler.onClick(movieItem);
        }
    }
}
