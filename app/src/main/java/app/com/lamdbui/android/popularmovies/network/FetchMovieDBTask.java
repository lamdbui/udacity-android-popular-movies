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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.lamdbui.android.popularmovies.BuildConfig;
import app.com.lamdbui.android.popularmovies.model.Movie;

/**
 * Created by lamdbui on 1/8/17.
 */

public class FetchMovieDBTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

    public static final String POPULAR_PARAM = "popular";
    public static final String TOP_RATED_PARAM = "top_rated";

    private OnCompletedFetchMovieDBTaskListener mCallback;

    public FetchMovieDBTask(OnCompletedFetchMovieDBTaskListener callback) {
        super();

        // attach callback from parent context
        mCallback = callback;
    }

    // Calling Activity or Fragment must implement this interface
    public interface OnCompletedFetchMovieDBTaskListener {
        public void completedFetchMovieDBTask(List<Movie> result);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(List<Movie> result) {
        mCallback.completedFetchMovieDBTask(result);
    }

    @Override
    protected List<Movie> doInBackground(String... params) {

        // We pass in the sort option in the params
        String sortOption = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieDbJsonStr = null;

        try {

            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
            //final String POPULAR_PARAM = "popular";
            //final String TOP_RATED_PARAM = "top_rated";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    //.appendPath((mSortOption == MovieListFragment.SortBy.POPULAR) ? POPULAR_PARAM : TOP_RATED_PARAM)
                    .appendPath(sortOption)
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
            movieDbJsonStr = buffer.toString();

            Log.d(LOG_TAG, "MovieDB JSON String: " + movieDbJsonStr);
        }
        catch(IOException e) {
            Log.e(LOG_TAG, "Error: ", e);
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
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        List<Movie> movieList = new ArrayList<>();

        // Lets parse the JSON string
        try {
            movieList = convertMovieDbJsonToMovie(movieDbJsonStr);
            Log.d(LOG_TAG, "MovieList ITEMS: " + movieList.size());
        }
        catch(JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
            e.printStackTrace();
        }

        return movieList;
    }

    // This function assumes we have a valid JSON string already
    private List<Movie> convertMovieDbJsonToMovie(String jsonStr) throws JSONException {
        List<Movie> movieList = new ArrayList<>();

        final String MOVIEDB_RESULTS = "results";
        final String MOVIEDB_POSTER_PATH = "poster_path";
        final String MOVIEDB_ADULT = "adult";
        final String MOVIEDB_OVERVIEW = "overview";
        final String MOVIEDB_RELEASE_DATE = "release_date";
        final String MOVIEDB_GENRE_IDS = "genre_ids";
        final String MOVIEDB_ID = "id";
        final String MOVIEDB_ORIGINAL_TITLE = "original_title";
        final String MOVIEDB_ORIGINAL_LANGUAGE = "original_language";
        final String MOVIEDB_TITLE = "title";
        final String MOVIEDB_BACKDROP_PATH = "backdrop_path";
        final String MOVIEDB_POPULARITY = "popularity";
        final String MOVIEDB_VOTE_COUNT = "vote_count";
        final String MOVIEDB_VIDEO = "video";
        final String MOVIEDB_VOTE_AVERAGE = "vote_average";

        JSONObject rootJsonObj = new JSONObject(jsonStr);
        JSONArray resultsArray = rootJsonObj.getJSONArray(MOVIEDB_RESULTS);

        for(int i = 0; i < resultsArray.length(); i++) {
            JSONObject currJsonMovie = resultsArray.getJSONObject(i);

            Movie movie = new Movie();
            movie.setPosterPath(currJsonMovie.getString(MOVIEDB_POSTER_PATH));
            movie.setAdult(currJsonMovie.getBoolean(MOVIEDB_ADULT));
            movie.setOverview(currJsonMovie.getString(MOVIEDB_OVERVIEW));
            movie.setId(currJsonMovie.getInt(MOVIEDB_ID));
            movie.setOriginalTitle(currJsonMovie.getString(MOVIEDB_ORIGINAL_TITLE));
            movie.setOriginalLanguage(currJsonMovie.getString(MOVIEDB_ORIGINAL_LANGUAGE));
            movie.setTitle(currJsonMovie.getString(MOVIEDB_TITLE));
            movie.setBackdropPath(currJsonMovie.getString(MOVIEDB_BACKDROP_PATH));
            movie.setPopularity(currJsonMovie.getDouble(MOVIEDB_POPULARITY));
            movie.setVoteCount(currJsonMovie.getInt(MOVIEDB_VOTE_COUNT));
            movie.setVideo(currJsonMovie.getBoolean(MOVIEDB_VIDEO));
            movie.setVoteAverage(currJsonMovie.getDouble(MOVIEDB_VOTE_AVERAGE));

            // special handling for genre list to get IDs
            JSONArray jsonGenreIds = currJsonMovie.getJSONArray(MOVIEDB_GENRE_IDS);
            List<Integer> genreIds = new ArrayList<>();
            for(int j = 0; j < jsonGenreIds.length(); j++) {
                genreIds.add(jsonGenreIds.getInt(j));
            }
            movie.setGenreIds(genreIds);

            // special handling for the release date to convert into Date object
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date releaseDate =
                        dateFormat.parse(currJsonMovie.getString(MOVIEDB_RELEASE_DATE));

                movie.setReleaseDate(releaseDate);
            }
            catch(ParseException e) {
                Log.e(LOG_TAG, "Error parsing JSON string", e);
            }

            movieList.add(movie);
        }

        return movieList;
    }
}
