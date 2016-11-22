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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

    public class FetchMovieDBPopularTask extends AsyncTask<Void, Void, String> {

        private final String LOG_TAG = FetchMovieDBPopularTask.class.getSimpleName();

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                // Need to parse the JSON here

            }
        }

        @Override
        protected String doInBackground(Void... params) {

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

            return null;
        }
    }

    private class MovieHolder extends RecyclerView.ViewHolder {

        private Movie mMovie;

        private ImageView mImageView;
        private TextView mTitleTextView;


        public MovieHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.list_item_movie_image_view);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_movie_text_view);
        }

        public void bindMovie(Movie movie) {
            mMovie = movie;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            mImageView.setImageBitmap(bitmap);
            mTitleTextView.setText(mMovie.getTitle());
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
    }
}
