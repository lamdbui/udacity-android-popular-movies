package app.com.lamdbui.android.popularmovies.network;

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

import app.com.lamdbui.android.popularmovies.BuildConfig;
import app.com.lamdbui.android.popularmovies.model.MovieTrailer;

/**
 * Created by lamdbui on 1/8/17.
 */

public class FetchMovieTrailersTask extends AsyncTask<String, Void, List<MovieTrailer>> {

    private static final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();

    private OnCompletedFetchMovieTrailerTaskListener mCallback;

    // Calling Activity or Fragment must implement this interface
    public interface OnCompletedFetchMovieTrailerTaskListener {
        public void completedFetchMovieTrailerTask(List<MovieTrailer> result);
    }

    public FetchMovieTrailersTask(OnCompletedFetchMovieTrailerTaskListener callback) {
        super();

        mCallback = callback;
    }

    @Override
    protected void onPostExecute(List<MovieTrailer> movieTrailers) {
        Log.d(LOG_TAG, "Num of Trailers = " + movieTrailers.size());
        mCallback.completedFetchMovieTrailerTask(movieTrailers);
    }

    @Override
    protected List<MovieTrailer> doInBackground(String... params) {

        // get the movie ID we will be querying
        String id = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieTrailerJsonStr = null;

        try {

            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";
            final String VIDEOS_PARAM = "videos";

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

            movieTrailerJsonStr = buffer.toString();

            Log.d(LOG_TAG, "Movie DB Video JSON: " + movieTrailerJsonStr);

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

        // if we got this far, we can do our actual parsing
        List<MovieTrailer> movieTrailers = new ArrayList<>();

        // Lets parse the JSON string
        try {
            movieTrailers = convertMovieDbJsonToMovieTrailers(movieTrailerJsonStr);
            Log.d(LOG_TAG, "MovieList ITEMS: " + movieTrailers.size());
        }
        catch(JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
            e.printStackTrace();
        }

        return movieTrailers;
    }

    private List<MovieTrailer> convertMovieDbJsonToMovieTrailers(String jsonStr)
        throws JSONException {

        List<MovieTrailer> movieTrailers = new ArrayList<>();

//        {
//            "id": 12,
//                "results": [
//            {
//                "id": "533ec651c3a3685448000010",
//                    "iso_639_1": "en",
//                    "iso_3166_1": "US",
//                    "key": "SPHfeNgogVs",
//                    "name": "Trailer 1",
//                    "site": "YouTube",
//                    "size": 720,
//                    "type": "Trailer"
//            }
//            ]
//        }

        final String MOVIEDB_ID = "id";
        final String MOVIEDB_RESULTS = "results";
        final String MOVIEDB_RESULT_ID = "id";
        final String MOVIEDB_RESULT_ISO_639_1 = "iso_639_1";
        final String MOVIEDB_RESULT_ISO_3166_1 = "iso_3166_1";
        final String MOVIEDB_RESULT_KEY = "key";
        final String MOVIEDB_RESULT_NAME = "name";
        final String MOVIEDB_RESULT_SITE = "site";
        final String MOVIEDB_RESULT_SIZE = "size";
        final String MOVIEDB_RESULT_TYPE = "type";

        JSONObject rootObject = new JSONObject(jsonStr);
        JSONArray resultsArray = rootObject.getJSONArray(MOVIEDB_RESULTS);

        for(int i = 0; i < resultsArray.length(); i++) {
            JSONObject resultObject = resultsArray.getJSONObject(i);

            MovieTrailer movieTrailer = new MovieTrailer();
            movieTrailer.setId(resultObject.getString(MOVIEDB_RESULT_ID));
            movieTrailer.setIso_639_1(resultObject.getString(MOVIEDB_RESULT_ISO_639_1));
            movieTrailer.setIso_3166_1(resultObject.getString(MOVIEDB_RESULT_ISO_3166_1));
            movieTrailer.setKey(resultObject.getString(MOVIEDB_RESULT_KEY));
            movieTrailer.setName(resultObject.getString(MOVIEDB_RESULT_NAME));
            movieTrailer.setSite(resultObject.getString(MOVIEDB_RESULT_SITE));
            movieTrailer.setSize(resultObject.getInt(MOVIEDB_RESULT_SIZE));
            movieTrailer.setType(resultObject.getString(MOVIEDB_RESULT_TYPE));

            movieTrailers.add(movieTrailer);
        }

        return movieTrailers;
    }
}
