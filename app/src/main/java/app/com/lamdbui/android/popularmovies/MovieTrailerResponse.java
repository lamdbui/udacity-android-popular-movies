package app.com.lamdbui.android.popularmovies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.com.lamdbui.android.popularmovies.model.MovieTrailer;

/**
 * Created by lamdbui on 1/23/17.
 */

public class MovieTrailerResponse {
    //    {
//        "id": 12,
//            "results": [
//        {
//            "id": "533ec651c3a3685448000010",
//                "iso_639_1": "en",
//                "iso_3166_1": "US",
//                "key": "SPHfeNgogVs",
//                "name": "Trailer 1"
//                "site": "YouTube",
//                "size": 720,
//                "type": "Trailer"
//        }
//        ]
//    }

    @SerializedName("id")
    private int mId;

    @SerializedName("results")
    private List<MovieTrailer> mResults;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public List<MovieTrailer> getResults() {
        return mResults;
    }

    public void setResults(List<MovieTrailer> results) {
        mResults = results;
    }
}
