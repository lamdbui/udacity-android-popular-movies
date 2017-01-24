package app.com.lamdbui.android.popularmovies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.com.lamdbui.android.popularmovies.model.MovieReview;

/**
 * Created by lamdbui on 1/23/17.
 */

public class MovieReviewResponse {

    @SerializedName("id")
    private int mId;
    @SerializedName("results")
    private List<MovieReview> mResults;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public List<MovieReview> getResults() {
        return mResults;
    }

    public void setResults(List<MovieReview> results) {
        mResults = results;
    }
}
