package app.com.lamdbui.android.popularmovies;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    public enum SortBy { POPULAR, TOP_RATED };

    private static final int REQUEST_SORT_BY = 0;
    private static final String DIALOG_SORT_BY = "DialogSortBy";
    private static final String ARG_SORT_BY = "sort_by";

    private RecyclerView mMovieListRecyclerView;
    private MovieAdapter mMovieAdapter;

    private SortBy mSortOption;

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_SORT_BY, mSortOption);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            mSortOption = (SortBy) savedInstanceState.getSerializable(ARG_SORT_BY);
        }
        else {
            // default setting
            mSortOption = SortBy.POPULAR;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_sort, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_item_sort_by) {
            FragmentManager manager = getFragmentManager();
            SortByFragment dialog = SortByFragment.newInstance(mSortOption);
            dialog.setTargetFragment(MovieListFragment.this, REQUEST_SORT_BY);
            dialog.show(manager, DIALOG_SORT_BY);
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mMovieListRecyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);
        mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_SORT_BY) {
            SortBy prevSortOption = mSortOption;
            mSortOption = (SortBy) data.getSerializableExtra(SortByFragment.EXTRA_SORT_BY);

            // only run the UI update if the value was actually changed
            if(prevSortOption != mSortOption) {
                updateUI();
            }
        }
    }

    // helper function to automatically update the UI
    private void updateUI() {

        getMovieDBList();

        // hook up the adapter to the RecyclerView
        if(mMovieAdapter == null) {

            List<Movie> movieList = new ArrayList<>();

            mMovieAdapter = new MovieAdapter(movieList);
            mMovieListRecyclerView.setAdapter(mMovieAdapter);
        }
        else {
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    // helper function to verify that we have a working and valid network connection
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getMovieDBList() {
        if(isOnline()) {
            FetchMovieDBTask movieDbTask = new FetchMovieDBTask();
            movieDbTask.execute();
        }
        // show a message if we don't have an active network connection available
        else {
            Toast.makeText(getActivity(), R.string.error_no_connection, Toast.LENGTH_LONG).show();
        }
    }

    public class FetchMovieDBTask extends AsyncTask<Void, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

        private ProgressDialog mProgressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            // show a nice dialog to the user indicating that something is happening
            mProgressDialog.setMessage(getString(R.string.fetch_movie_list_message));
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(List<Movie> result) {

            if(mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
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
                final String TOP_RATED_PARAM = "top_rated";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendPath((mSortOption == SortBy.POPULAR) ? POPULAR_PARAM : TOP_RATED_PARAM)
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

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Movie mMovie;

        private ImageView mImageView;


        public MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_movie_image_view);
            mImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent movieIntent = MovieDetailActivity.newIntent(getActivity(), mMovie);
            startActivity(movieIntent);
        }

        public void bindMovie(Movie movie) {
            mMovie = movie;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            final String MOVIEDB_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

            Uri imageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                    .appendEncodedPath(mMovie.getPosterPath())
                    .build();

            // so that the image fills the entire space
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
