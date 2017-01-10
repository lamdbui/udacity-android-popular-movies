package app.com.lamdbui.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment
    implements FetchMovieTrailersTask.OnCompletedFetchMovieTrailerTaskListener {

    private static final String ARG_MOVIE_PARCEL = "movie";

    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseDateTextView;
    private TextView mTrailersTextView;

    private Movie mMovie;
    private List<MovieTrailer> mMovieTrailers;

    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE_PARCEL, movie);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void completedFetchMovieTrailerTask(List<MovieTrailer> result) {
        if(result != null) {
            mMovieTrailers = result;

            updateUI();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE_PARCEL);
        mMovieTrailers = new ArrayList<>();

        // Fetch our trailers (and soon reviews)
        FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask(this);
        fetchMovieTrailersTask.execute(Integer.toString(mMovie.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mTitleTextView = (TextView) view.findViewById(R.id.movie_detail_title);
        mPosterImageView = (ImageView) view.findViewById(R.id.movie_detail_poster);
        mVoteAverageTextView = (TextView) view.findViewById(R.id.movie_detail_vote_average);
        mOverviewTextView = (TextView) view.findViewById(R.id.movie_detail_overview);
        mReleaseDateTextView = (TextView) view.findViewById(R.id.movie_detail_release_date);
        mTrailersTextView = (TextView) view.findViewById(R.id.movie_trailer_text);

        updateUI();

        return view;
    }

    private void updateUI() {

        // Fetch our trailers (and soon reviews)
        //FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask();
        //fetchMovieTrailersTask.execute(Integer.toString(mMovie.getId()));

        mTitleTextView.setText(mMovie.getTitle());

        final String MOVIEDB_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

        Uri imageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                .appendEncodedPath(mMovie.getPosterPath())
                .build();
        Picasso.with(getActivity()).load(imageLocation.toString()).into(mPosterImageView);
        mOverviewTextView.setText(mMovie.getOverview());

        String voteAverageStr = Double.toString(mMovie.getVoteAverage()) + "/10 ("
                + getString(R.string.movie_user_rating) + ")";
        mVoteAverageTextView.setText(voteAverageStr);

        SimpleDateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = mMovie.getReleaseDate();
        mReleaseDateTextView.setText(releaseDateFormat.format(releaseDate));

        mTrailersTextView.setText("Num of Trailers = " + mMovieTrailers.size());

        Log.d("MOVIE_DETAIL_FRAGMENT", "LOLWUT");
    }
}
