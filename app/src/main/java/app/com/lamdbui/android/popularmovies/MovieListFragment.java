package app.com.lamdbui.android.popularmovies;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import app.com.lamdbui.android.popularmovies.data.MovieContract.MovieTable;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment
    implements FetchMovieDBTask.OnCompletedFetchMovieDBTaskListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    public enum SortBy { POPULAR, TOP_RATED, FAVORITES };

    private static final int MOVIE_LOADER = 0;

    private static final int REQUEST_SORT_BY = 0;
    private static final String DIALOG_SORT_BY = "DialogSortBy";
    private static final String ARG_SORT_BY = "sort_by";
    private static final String ARG_MOVIE_LIST = "movies";

    private RecyclerView mMovieListRecyclerView;
    private MovieAdapter mMovieAdapter;

    private SortBy mSortOption;

    private ProgressDialog mProgressDialog;

    private boolean mFetchData;

    List<Movie> mMovies;

    public MovieListFragment() {
        // Required empty public constructor
    }

    /* Start FetchMovieDbTask implementation */

    @Override
    public void completedFetchMovieDBTask(List<Movie> result) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (result != null) {
            Toast.makeText(getActivity(),
                    "FetchMovieDBTask returned this many: " + result.size(), Toast.LENGTH_SHORT)
                    .show();

            mMovies = result;

            mMovieAdapter.setMovies(result);
            mMovieAdapter.notifyDataSetChanged();
        }
        // result == null
        else {
            Toast.makeText(getActivity(), R.string.error_general,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /* Start LoaderManager.LoaderCallbacks<Cursor> interface implemention */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(
                this.getActivity(),
                MovieTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();

        List<Movie> movies = new ArrayList<>();

        while(!data.isAfterLast()) {

            Movie movie = new Movie();

            movie.setId(data.getInt(data.getColumnIndex(MovieTable.COLS.ID)));
            // special handling for the release date to convert into Date object
            Date movieDate =
                    Movie.convertStringToDate(data.getString(data.getColumnIndex(MovieTable.COLS.RELEASE_DATE)));
            movie.setReleaseDate(movieDate);
            movie.setTitle(data.getString(data.getColumnIndex(MovieTable.COLS.TITLE)));
            movie.setOriginalTitle(data.getString(data.getColumnIndex(MovieTable.COLS.ORIGINAL_TITLE)));
            movie.setOriginalLanguage(data.getString(data.getColumnIndex(MovieTable.COLS.ORIGINAL_LANGUAGE)));
            movie.setPopularity(data.getDouble(data.getColumnIndex(MovieTable.COLS.POPULARITY)));
            movie.setVoteCount(data.getInt(data.getColumnIndex(MovieTable.COLS.VOTE_COUNT)));
            int video = data.getInt(data.getColumnIndex(MovieTable.COLS.VIDEO));
            movie.setVideo((video != 0) ? true : false);
            movie.setVoteAverage(data.getDouble(data.getColumnIndex(MovieTable.COLS.VOTE_AVERAGE)));
            movie.setPosterPath(data.getString(data.getColumnIndex(MovieTable.COLS.POSTER_PATH)));
            movie.setBackdropPath(data.getString(data.getColumnIndex(MovieTable.COLS.BACKDROP_PATH)));
            movie.setOverview(data.getString(data.getColumnIndex(MovieTable.COLS.OVERVIEW)));
            int adult = data.getInt(data.getColumnIndex(MovieTable.COLS.ADULT));
            movie.setAdult((adult != 0) ? true : false);
            int favorite = data.getInt(data.getColumnIndex(MovieTable.COLS.FAVORITE));
            movie.setFavorite((favorite != 0) ? true : false);

            movies.add(movie);

            data.moveToNext();
        }

        mMovies = movies;
        mMovieAdapter.setMovies(mMovies);
        mMovieAdapter.notifyDataSetChanged();

        updateUI(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /* Start MovieListFragment methods */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_SORT_BY, mSortOption);
        outState.putParcelableArrayList(ARG_MOVIE_LIST, (ArrayList<Movie>) mMovies);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null) {
            mSortOption = (SortBy) savedInstanceState.getSerializable(ARG_SORT_BY);
            mMovies = savedInstanceState.getParcelableArrayList(ARG_MOVIE_LIST);
        }
        else {
            // default setting
            mSortOption = SortBy.POPULAR;

            // should this be outside?
            mProgressDialog = new ProgressDialog(getActivity());

            mFetchData = true;

            mMovies = new ArrayList<>();
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
        //mMovieListRecyclerView.setHasFixedSize(true);
        //mMovieListRecyclerView.setItemViewCacheSize(20);
        //mMovieListRecyclerView.setDrawingCacheEnabled(true);

        //mMovieListRecyclerView.setAdapter(mMovieAdapter);

        updateUI(mFetchData);

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
                mFetchData = true;
                updateUI(mFetchData);
            }
        }
    }

    // helper function to automatically update the UI
    private void updateUI(boolean fetchData) {

        if(fetchData) {
            getMovieDBList();

            // disable so we don't keep fetching Data
            mFetchData = false;
        }

        // hook up the adapter to the RecyclerView
        if(mMovieAdapter == null) {

            //List<Movie> movieList = new ArrayList<>();

            //mMovieAdapter = new MovieAdapter(movieList);
            mMovieAdapter = new MovieAdapter(mMovies);
            mMovieListRecyclerView.setAdapter(mMovieAdapter);
        }
        else {


            mMovieAdapter.setMovies(mMovies);
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

            if(mSortOption != SortBy.FAVORITES) {

                // show a nice dialog to the user indicating that something is happening
                if(mProgressDialog != null) {
                    mProgressDialog.setMessage(getString(R.string.fetch_movie_list_message));
                    mProgressDialog.show();
                }

                FetchMovieDBTask movieDbTask = new FetchMovieDBTask(this);
                movieDbTask.execute(
                        (mSortOption == MovieListFragment.SortBy.POPULAR) ?
                                FetchMovieDBTask.POPULAR_PARAM : FetchMovieDBTask.TOP_RATED_PARAM
                );
            }
            // SortBy.FAVORITES
            else {
                getLoaderManager().initLoader(MOVIE_LOADER, null, this);
            }
        }
        // show a message if we don't have an active network connection available
        else {
            Toast.makeText(getActivity(), R.string.error_no_connection, Toast.LENGTH_LONG).show();
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
            // check to see if it was a favorite
            Cursor queryCursor = getContext().getContentResolver().query(MovieTable.CONTENT_URI, null,
                    MovieTable.COLS.ID + " = ?",
                    new String[] { Integer.toString(mMovie.getId()) },
                    null
            );

            if(queryCursor.getCount() > 0)
                mMovie.setFavorite(true);
            else
                mMovie.setFavorite(false);

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

            //DisplayMetrics metrics = getResources().getDisplayMetrics();

            //Log.d("DISPLAY_METRICS", "W/H: " + metrics.widthPixels + "/" + metrics.heightPixels);

            Picasso picasso = Picasso.with(getActivity());
            picasso.setIndicatorsEnabled(true);

            picasso.load(imageLocation.toString())
                    .into(mImageView);
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