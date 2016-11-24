package app.com.lamdbui.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE_PARCEL = "movie";

    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mOverviewTextView;

    private Movie mMovie;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE_PARCEL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mTitleTextView = (TextView) view.findViewById(R.id.movie_detail_title);
        mPosterImageView = (ImageView) view.findViewById(R.id.movie_detail_poster);
        mOverviewTextView = (TextView) view.findViewById(R.id.movie_detail_overview);

        updateUI();

        return view;
    }

    private void updateUI() {
        mTitleTextView.setText(mMovie.getTitle());
        //mPosterImageView.setIm
        //Picasso.with(getActivity()).load(imageLocation.toString()).into(mImageView);
        final String MOVIEDB_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

        Uri imageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                .appendEncodedPath(mMovie.getPosterPath())
                .build();
        Picasso.with(getActivity()).load(imageLocation.toString()).into(mPosterImageView);
        mOverviewTextView.setText(mMovie.getOverview());
    }

}
