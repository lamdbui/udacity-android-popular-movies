package app.com.lamdbui.android.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment
    implements FetchMovieTrailersTask.OnCompletedFetchMovieTrailerTaskListener,
            FetchMovieReviewsTask.OnCompletedFetchMovieReviewsTaskListener {

    private static final String ARG_MOVIE_PARCEL = "movie";

    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseDateTextView;
    private TextView mTrailersTextView;
    private TextView mReviewsTextView;

    private RecyclerView mTrailersRecyclerView;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    private RecyclerView mReviewsRecyclerView;
    private MovieReviewAdapter mMovieReviewAdapter;

    private Movie mMovie;
    private List<MovieTrailer> mMovieTrailers;
    private List<MovieReview> mMovieReviews;

    private TextView mEmptyTrailersTextView;
    private TextView mEmptyReviewsTextView;

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
    public void completedFetchMovieReviewsTask(List<MovieReview> result) {
        if(result != null) {
            mMovieReviews = result;

            updateUI();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovie = (Movie) getArguments().getParcelable(ARG_MOVIE_PARCEL);
        mMovieTrailers = new ArrayList<>();
        mMovieReviews = new ArrayList<>();

        // Fetch our trailers
        FetchMovieTrailersTask fetchMovieTrailersTask = new FetchMovieTrailersTask(this);
        fetchMovieTrailersTask.execute(Integer.toString(mMovie.getId()));

        // Fetch our reviews
        FetchMovieReviewsTask fetchMovieReviewsTask = new FetchMovieReviewsTask(this);
        fetchMovieReviewsTask.execute(Integer.toString(mMovie.getId()));
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
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.movie_trailer_recyclerview);
        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(getActivity());
        mTrailersRecyclerView.setLayoutManager(layoutManagerTrailers);

        DividerItemDecoration dividerTrailers = new DividerItemDecoration(getContext(),
                layoutManagerTrailers.getOrientation());
        mTrailersRecyclerView.addItemDecoration(dividerTrailers);

        mReviewsTextView = (TextView) view.findViewById(R.id.movie_review_text);

        mReviewsRecyclerView = (RecyclerView) view.findViewById(R.id.movie_review_recyclerview);
        LinearLayoutManager layoutManagerReviews = new LinearLayoutManager(getActivity());
        mReviewsRecyclerView.setLayoutManager(layoutManagerReviews);

        DividerItemDecoration dividerReviews = new DividerItemDecoration(getContext(),
                layoutManagerReviews.getOrientation());
        mReviewsRecyclerView.addItemDecoration(dividerReviews);

        mEmptyTrailersTextView = (TextView) view.findViewById(R.id.empty_movie_trailer_text);
        mEmptyReviewsTextView = (TextView) view.findViewById(R.id.empty_movie_review_text);

        updateUI();

        return view;
    }

    private void updateUI() {

        // hook up our adapter to the RecyclerView
        if(mMovieTrailerAdapter == null) {

            List<MovieTrailer> movieTrailerList = new ArrayList<>();
            mMovieTrailerAdapter = new MovieTrailerAdapter(movieTrailerList);
            mTrailersRecyclerView.setAdapter(mMovieTrailerAdapter);

            List<MovieReview> movieReviewList = new ArrayList<>();
            mMovieReviewAdapter = new MovieReviewAdapter(movieReviewList);
            mReviewsRecyclerView.setAdapter(mMovieReviewAdapter);

        }
        else {
            mMovieTrailerAdapter.setMovieTrailers(mMovieTrailers);
            mMovieTrailerAdapter.notifyDataSetChanged();

            mMovieReviewAdapter.setMovieReviews(mMovieReviews);
            mMovieReviewAdapter.notifyDataSetChanged();
        }

        // add some handling so we can show an empty item when there are no list items
        if(mMovieTrailers.isEmpty()) {
            mTrailersRecyclerView.setVisibility(GONE);
            mEmptyTrailersTextView.setVisibility(VISIBLE);
        }
        else {
            mTrailersRecyclerView.setVisibility(VISIBLE);
            mEmptyTrailersTextView.setVisibility(GONE);
        }

        if(mMovieReviews.isEmpty()) {
            mReviewsRecyclerView.setVisibility(GONE);
            mEmptyReviewsTextView.setVisibility(VISIBLE);
        }
        else {
            mReviewsRecyclerView.setVisibility(VISIBLE);
            mEmptyReviewsTextView.setVisibility(GONE);
        }

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

        mTrailersTextView.setText(R.string.trailers);

        mReviewsTextView.setText(R.string.reviews);
    }

    private class MovieTrailerHolder extends RecyclerView.ViewHolder {

        private ImageButton mMovieTrailerPlayImageView;
        private TextView mMovieTrailerNameTextView;

        private MovieTrailer mMovieTrailer;

        public MovieTrailerHolder(View itemView) {
            super(itemView);

            mMovieTrailerPlayImageView =
                    (ImageButton) itemView.findViewById(R.id.list_item_movie_trailer_play_icon);
            mMovieTrailerPlayImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String YOUTUBE_BASE_NAME = "https://www.youtube.com/watch?v=";
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(YOUTUBE_BASE_NAME + mMovieTrailer.getKey()));
                    if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            mMovieTrailerNameTextView =
                    (TextView) itemView.findViewById(R.id.list_item_movie_trailer_name);
        }

        public void bindMovieTrailer(MovieTrailer movieTrailer) {
            mMovieTrailer = movieTrailer;

            mMovieTrailerNameTextView.setText(movieTrailer.getName());
        }
    }

    private class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerHolder> {

        private List<MovieTrailer> mMovieTrailers;

        public MovieTrailerAdapter(List<MovieTrailer> movieTrailers) {
            super();
            mMovieTrailers = movieTrailers;
        }

        @Override
        public MovieTrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_movie_trailer, parent, false);

            return new MovieTrailerHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieTrailerHolder holder, int position) {
            MovieTrailer movieTrailer = mMovieTrailers.get(position);
            holder.bindMovieTrailer(movieTrailer);
        }

        @Override
        public int getItemCount() {
            return mMovieTrailers.size();
        }

        public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
            mMovieTrailers = movieTrailers;
        }
    }

    private class MovieReviewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        private TextView mTextAuthor;
        private TextView mTextContent;

        private MovieReview mReview;

        public MovieReviewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTextAuthor = (TextView) itemView.findViewById(R.id.list_item_movie_review_author);
            mTextContent = (TextView) itemView.findViewById(R.id.list_item_movie_review_content);
        }

        @Override
        public void onClick(View view) {
            Intent intent = MovieReviewListActivity
                    .newIntent(getActivity(), (ArrayList) mMovieReviews);
            startActivity(intent);
        }

        public void bindMovieReview(MovieReview movieReview) {
            mReview = movieReview;

            mTextAuthor.setText(mReview.getReviewAuthor());
            mTextContent.setText(mReview.getReviewContent());
        }
    }

    private class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewHolder> {

        private List<MovieReview> mReviews;

        public MovieReviewAdapter(List<MovieReview> reviews) {
            super();

            mReviews = reviews;
        }

        @Override
        public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(
                    R.layout.list_item_movie_review,
                    parent,
                    false);

            return new MovieReviewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieReviewHolder holder, int position) {
            MovieReview review = mReviews.get(position);
            holder.bindMovieReview(review);
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }

        public void setMovieReviews(List<MovieReview> reviews) {
            mReviews = reviews;
        }
    }
}
