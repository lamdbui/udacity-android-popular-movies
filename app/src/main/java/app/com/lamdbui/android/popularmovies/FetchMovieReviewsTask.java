package app.com.lamdbui.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lamdbui on 1/10/17.
 */

public class FetchMovieReviewsTask extends AsyncTask<String, Void, List<MovieReview>> {

    private static final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

    // Calling Activity or Fragment needs to implement this!
    private FetchMovieReviewsTask.OnCompletedFetchMovieReviewsTaskListener mCallback;

    public interface OnCompletedFetchMovieReviewsTaskListener {
        public void completedFetchMovieReviewsTask(List<MovieReview> reviews);
    }

    public FetchMovieReviewsTask(
            FetchMovieReviewsTask.OnCompletedFetchMovieReviewsTaskListener callback) {
        super();

        mCallback = callback;
    }

    @Override
    protected void onPostExecute(List<MovieReview> movieReviews) {
        super.onPostExecute(movieReviews);

        mCallback.completedFetchMovieReviewsTask(movieReviews);
    }

    @Override
    protected List<MovieReview> doInBackground(String... params) {

        // get the movie ID we will be querying
        String id = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieReviewJsonStr = null;

        try {

            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";
            final String VIDEOS_PARAM = "reviews";

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath(id)
                    .appendPath(VIDEOS_PARAM)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "MovieDB URL: " + builtUri.toString());

            // create our connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if(buffer == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                // we had an empty stream, so don't bother parsing
                return null;
            }

            movieReviewJsonStr = buffer.toString();

            Log.d(LOG_TAG, "Movie DB Reviews JSON: " + movieReviewJsonStr);

        }
        catch(IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    Log.e(LOG_TAG, "Error closing reader stream");
                }
            }
        }

        // if we got this far, we can do actual parsing
        List<MovieReview> reviews = new ArrayList<>();

        // let's parse the JSON string
        try {
            reviews = convertMovieDbJsonToMovieReviews(movieReviewJsonStr);
            Log.d(LOG_TAG, "Movie Reviews = " + reviews.size());
        }
        catch(JSONException e) {
            Log.d(LOG_TAG, "Error parsing JSON!", e);
        }

        return reviews;
    }

    private List<MovieReview> convertMovieDbJsonToMovieReviews(String jsonStr)
        throws JSONException {

        List<MovieReview> reviews = new ArrayList<>();

//    {
//        "id": 12,
//            "page": 1,
//            "results": [
//        {
//            "id": "52d4a742760ee30e2d0dac9d",
//                "author": "Dave09",
//                "content": "One of the best animated films I have ever seen. Great characters, amusing animation, and laugh-out-loud humor. Also, watch for the little skit shown after the credits. It's all great stuff that simply must be seen.",
//                "url": "https://www.themoviedb.org/review/52d4a742760ee30e2d0dac9d"
//        }
//        ],
//        "total_pages": 1,
//            "total_results": 1
//    }

        final String MOVIEDB_ID = "id";
        final String MOVIEDB_RESULTS = "results";
        final String MOVIEDB_RESULT_ID = "id";
        final String MOVIEDB_RESULT_AUTHOR = "author";
        final String MOVIEDB_RESULT_CONTENT = "content";
        final String MOVIEDB_RESULT_URL = "url";

        JSONObject rootObject = new JSONObject(jsonStr);
        JSONArray resultsArray = rootObject.getJSONArray(MOVIEDB_RESULTS);

        for(int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultObject = resultsArray.getJSONObject(i);

            MovieReview review = new MovieReview();
            review.setID(resultObject.getInt(MOVIEDB_RESULT_ID));
            review.setReviewAuthor(resultObject.getString(MOVIEDB_RESULT_AUTHOR));
            review.setReviewContent(resultObject.getString(MOVIEDB_RESULT_CONTENT));
            review.setReviewUrl(resultObject.getString(MOVIEDB_RESULT_URL));

            reviews.add(review);
        }

        return reviews;
    }
}
