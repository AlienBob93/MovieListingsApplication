package com.alien.prashantrao.popmovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prashant Rao on 12-Jun-17.
 */

public class MovieItem implements Parcelable{

    /**
     * Parcelable class to hold movie details
     */
    private String mTitle;
    private String mOriginalLanguage;
    private String mMovieOverview;
    private String mReleaseDate;
    private String mPosterPath;

    private long mVoteCount;
    private int mVoteAvg;

    private long mMovieId;

    public MovieItem(String title, String originalLanguage, String movieOverview,
                     String releaseDate, String posterPath, long voteCount, int voteAvg, long movieId) {

        this.mTitle = title;
        this.mOriginalLanguage = originalLanguage;
        this.mMovieOverview = movieOverview;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;

        this.mVoteCount = voteCount;
        this.mVoteAvg = voteAvg;
        this.mMovieId = movieId;
    }

    // Parcelling part
    public MovieItem(Parcel in) {
        mTitle = in.readString();
        mOriginalLanguage = in.readString();
        mMovieOverview = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mVoteCount = in.readLong();
        mVoteAvg = in.readInt();
        mMovieId = in.readLong();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mMovieOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeLong(mVoteCount);
        dest.writeInt(mVoteAvg);
        dest.writeLong(mMovieId);
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getDescription() {
        return mMovieOverview;
    }

    public int getRatings() {
        return mVoteAvg;
    }

    public long getVoteCount() {
        return mVoteCount;
    }

    public long getMovieId() {
        return mMovieId;
    }
}
