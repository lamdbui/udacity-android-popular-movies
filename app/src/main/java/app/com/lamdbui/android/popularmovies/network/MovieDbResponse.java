package app.com.lamdbui.android.popularmovies.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.com.lamdbui.android.popularmovies.model.Movie;

/**
 * Created by lamdbui on 1/23/17.
 */

public class MovieDbResponse {

    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }
}
