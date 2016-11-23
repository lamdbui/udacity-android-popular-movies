package app.com.lamdbui.android.popularmovies;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private RecyclerView mMovieListRecyclerView;
    private MovieAdapter mMovieAdapter;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mMovieListRecyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);
        //mMovieListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        // hook up the adapter to the RecyclerView
        if(mMovieAdapter == null) {

            List<Movie> dummyMovieList = new ArrayList<>();

            // create some dummy data
            for(int i = 0; i < 15; i++) {
                Movie dummyMovie = new Movie();
                dummyMovie.setId(i);
                dummyMovie.setTitle("Title #" + i);
                dummyMovie.setPosterPath("/xfWac8MTYDxujaxgPVcRD9yZaul.jpg");
                dummyMovie.setBackdropPath("/tFI8VLMgSTTU38i8TIsklfqS9Nl.jpg");
                dummyMovieList.add(dummyMovie);
            }

            mMovieAdapter = new MovieAdapter(dummyMovieList);
            mMovieListRecyclerView.setAdapter(mMovieAdapter);
        }
        else {
            mMovieAdapter.notifyDataSetChanged();
        }

        getMovieDBPopular();


        return view;
    }

    private void getMovieDBPopular() {
        FetchMovieDBPopularTask movieDbTask = new FetchMovieDBPopularTask();
        movieDbTask.execute();
    }

    public class FetchMovieDBPopularTask extends AsyncTask<Void, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDBPopularTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<Movie> result) {
            if(result != null) {
                mMovieAdapter.setMovies(result);
                mMovieAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected List<Movie> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieDbJsonStr = null;

            try {

                final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String POPULAR_PARAM = "popular";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath(POPULAR_PARAM)
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

    private class MovieHolder extends RecyclerView.ViewHolder {

        private Movie mMovie;

        private ImageView mImageView;
        //private TextView mTitleTextView;


        public MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_movie_image_view);
            //mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_movie_text_view);
        }

        public void bindMovie(Movie movie) {
            mMovie = movie;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            final String MOVIEDB_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

            Uri imageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                    .appendEncodedPath(mMovie.getPosterPath())
                    .build();

            mImageView.setAdjustViewBounds(true);

            Picasso.with(getActivity()).load(imageLocation.toString()).into(mImageView);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<Movie> mMovies;

        public MovieAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_movie, parent, false);

            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieHolder holder, int position) {
            Movie movie = mMovies.get(position);
            holder.bindMovie(movie);
        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        public void setMovies(List<Movie> movies) {
            mMovies = movies;
        }
    }
}
