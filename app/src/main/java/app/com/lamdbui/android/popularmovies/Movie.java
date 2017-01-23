package app.com.lamdbui.android.popularmovies;

import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lamdbui on 11/21/16.
 */

public class Movie implements Parcelable {

    private static final String LOG_TAG = Movie.class.getSimpleName();

    private int mId;
    private Date mReleaseDate;
    private List<Integer> mGenreIds;
    private String mTitle;
    private String mOriginalTitle;
    private String mOriginalLanguage;
    private double mPopularity;
    private int mVoteCount;
    private boolean mVideo;
    private double mVoteAverage;
    private String mPosterPath;
    private String mBackdropPath;
    private String mOverview;
    private boolean mAdult;
    private boolean mIsFavorite;

    private Movie(Parcel in) {
        mId = in.readInt();
        mReleaseDate = (Date) in.readSerializable();
        List<Integer> newGenreIds = new ArrayList<>();
        in.readList(newGenreIds, Integer.class.getClassLoader());
        mGenreIds = newGenreIds;
        mTitle = in.readString();
        mOriginalTitle = in.readString();
        mOriginalLanguage = in.readString();
        mPopularity = in.readDouble();
        mVoteCount = in.readInt();
        mVideo = (in.readInt() == 1) ? true : false;
        mVoteAverage = in.readDouble();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mOverview = in.readString();
        mAdult = (in.readInt() == 1) ? true : false;
        mIsFavorite = (in.readInt() == 1) ? true : false;
    }

    public Movie() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeSerializable(mReleaseDate);
        dest.writeList(mGenreIds);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOriginalLanguage);
        dest.writeDouble(mPopularity);
        dest.writeInt(mVoteCount);
        dest.writeInt(mVideo ? 1 : 0);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mOverview);
        dest.writeInt(mAdult ? 1 : 0);
        dest.writeInt(mIsFavorite ? 1 : 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        mReleaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        mGenreIds = genreIds;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        mOriginalLanguage = originalLanguage;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(int voteCount) {
        mVoteCount = voteCount;
    }

    public boolean isVideo() {
        return mVideo;
    }

    public void setVideo(boolean video) {
        mVideo = video;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public boolean isAdult() {
        return mAdult;
    }

    public void setAdult(boolean adult) {
        mAdult = adult;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public static String convertDateToString(Date date) {
        SimpleDateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = date;

        return releaseDateFormat.format(releaseDate);
    }

    public static Date convertStringToDate(String dateString) {

        Date releaseDate = new Date();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            releaseDate =
                    dateFormat.parse(dateString);
        }
        catch(ParseException e) {
            Log.d(LOG_TAG, "Error parsing dateString into Date: " + dateString);
        }
        return releaseDate;
    }
}
