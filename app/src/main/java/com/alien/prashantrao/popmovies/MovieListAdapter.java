package com.alien.prashantrao.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alien.prashantrao.popmovies.utilities.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Prashant Rao on 11-Jun-17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.PopularMovieList> {

    private static final String TAG = MovieListAdapter.class.getSimpleName();

    private ArrayList<MovieItem> mMovieList;

    final private PopularMovieListOnClickHandler mOnClickHandler;


    public MovieListAdapter(PopularMovieListOnClickHandler clickHandler) {
        mOnClickHandler = clickHandler;
    }

    public interface PopularMovieListOnClickHandler {
        void onClick(MovieItem movieItem);
    }

    public void setMovies(ArrayList<MovieItem> movies) {
        mMovieList = movies;
        notifyDataSetChanged();
    }

    @Override
    public PopularMovieList onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        PopularMovieList viewHolder = new PopularMovieList(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     */
    @Override
    public void onBindViewHolder(PopularMovieList holder, int position) {
        Context context = holder.itemView.getContext();
        MovieItem movie = mMovieList.get(position);

        holder.movie_title.setText(movie.getTitle());
        // load poster with placeholders
        Picasso.with(context)
                .load(context.getString(R.string.movie_poster_342px_base_url) + movie.getPosterPath())
                .placeholder(R.drawable.ic_local_movies_black_48dp).into(holder.movie_poster);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) {
            return 0;
        } else return mMovieList.size();
    }

    /**
     * Cache of the children views for the movie list.
     */
    public class PopularMovieList extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView movie_poster;
        private final TextView movie_title;

        public PopularMovieList(View itemView) {
            super(itemView);

            movie_poster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            movie_title = (TextView) itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            MovieItem movieItem = mMovieList.get(clickedPosition);
            mOnClickHandler.onClick(movieItem);
        }
    }
}
