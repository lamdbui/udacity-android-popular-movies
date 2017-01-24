package app.com.lamdbui.android.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.lamdbui.android.popularmovies.data.MovieContract;
import app.com.lamdbui.android.popularmovies.model.Movie;
import app.com.lamdbui.android.popularmovies.model.MovieReview;
import app.com.lamdbui.android.popularmovies.model.MovieTrailer;
import app.com.lamdbui.android.popularmovies.network.FetchMovieReviewsTask;
import app.com.lamdbui.android.popularmovies.network.FetchMovieTrailersTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment
    implements FetchMovieTrailersTask.OnCompletedFetchMovieTrailerTaskListener,
            FetchMovieReviewsTask.OnCompletedFetchMovieReviewsTaskListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String ARG_MOVIE_PARCEL = "movie";

    private ShareActionProvider mShareActionProvider;

    private ImageView mBackdropImageView;
    private TextView mTitleTextView;
    private ImageView mPosterImageView;
    private TextView mVoteAverageTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseDateTextView;
    private TextView mTrailersTextView;
    private TextView mReviewsTextView;
    private Button mFavoriteButton;

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

            // setup to share the first trailer
            if(result.size() > 0) {
                MovieTrailer firstTrailer = mMovieTrailers.get(0);
                setShareTrailerIntent(firstTrailer);
            }
            else {
                setShareTrailerIntent(null);
            }

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

        setHasOptionsMenu(true);

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

        mBackdropImageView = (ImageView) view.findViewById(R.id.movie_background_image);
        mTitleTextView = (TextView) view.findViewById(R.id.movie_detail_title);
        mPosterImageView = (ImageView) view.findViewById(R.id.movie_detail_poster);
        mVoteAverageTextView = (TextView) view.findViewById(R.id.movie_detail_vote_average);
        mOverviewTextView = (TextView) view.findViewById(R.id.movie_detail_overview);
        mReleaseDateTextView = (TextView) view.findViewById(R.id.movie_detail_release_date);
        mTrailersTextView = (TextView) view.findViewById(R.id.movie_trailer_text);
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.movie_trailer_recyclerview);
        LinearLayoutManager layoutManagerTrailers = new LinearLayoutManager(getActivity());
        mTrailersRecyclerView.setLayoutManager(layoutManagerTrailers);

        mFavoriteButton = (Button) view.findViewById(R.id.movie_favorite_button);
        mFavoriteButton.setBackgroundColor((mMovie.isFavorite()) ?
                getResources().getColor(R.color.colorRemoveFavorite) :
                getResources().getColor(R.color.colorAddFavorite));
        mFavoriteButton.setText((mMovie.isFavorite()) ?
                getString(R.string.remove_from_favorites) : getString(R.string.add_to_favorites));
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite();
            }
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_action_share);

        // not sure why the right one wasn't being included, so had to specify directly in cast
        mShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // setup to share the first trailer
        // this is in case the task finishes before the UI loads
        if(mMovieTrailers.size() > 0) {
            MovieTrailer firstTrailer = mMovieTrailers.get(0);
            setShareTrailerIntent(firstTrailer);
        }
        else {
            setShareTrailerIntent(null);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void toggleFavorite() {
        if(!mMovie.isFavorite()) {

            mMovie.setFavorite(!mMovie.isFavorite());

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieTable.COLS.ID, mMovie.getId());
            String movieReleaseDateString = Movie.convertDateToString(mMovie.getReleaseDate());
            movieValues.put(MovieContract.MovieTable.COLS.RELEASE_DATE, movieReleaseDateString);
            movieValues.put(MovieContract.MovieTable.COLS.TITLE, mMovie.getTitle());
            movieValues.put(MovieContract.MovieTable.COLS.ORIGINAL_TITLE, mMovie.getOriginalTitle());
            movieValues.put(MovieContract.MovieTable.COLS.ORIGINAL_LANGUAGE, mMovie.getOriginalLanguage());
            movieValues.put(MovieContract.MovieTable.COLS.POPULARITY, mMovie.getPopularity());
            movieValues.put(MovieContract.MovieTable.COLS.VOTE_COUNT, mMovie.getVoteCount());
            movieValues.put(MovieContract.MovieTable.COLS.VIDEO, mMovie.isVideo());
            movieValues.put(MovieContract.MovieTable.COLS.VOTE_AVERAGE, mMovie.getVoteAverage());
            movieValues.put(MovieContract.MovieTable.COLS.POSTER_PATH, mMovie.getPosterPath());
            movieValues.put(MovieContract.MovieTable.COLS.BACKDROP_PATH, mMovie.getBackdropPath());
            movieValues.put(MovieContract.MovieTable.COLS.OVERVIEW, mMovie.getOverview());
            movieValues.put(MovieContract.MovieTable.COLS.ADULT, mMovie.isAdult());
            movieValues.put(MovieContract.MovieTable.COLS.FAVORITE, mMovie.isFavorite());

            getContext().getContentResolver().insert(
                    MovieContract.MovieTable.CONTENT_URI,
                    movieValues
            );

            mFavoriteButton.setText(getString(R.string.remove_from_favorites));
        }
        else {

            mMovie.setFavorite(!mMovie.isFavorite());

            getContext().getContentResolver().delete(
                    MovieContract.MovieTable.CONTENT_URI,
                    MovieContract.MovieTable.COLS.ID + " = ?",
                    new String[] { Integer.toString(mMovie.getId()) }
            );

            mFavoriteButton.setText(getString(R.string.add_to_favorites));
        }

        mFavoriteButton.setBackgroundColor((mMovie.isFavorite()) ?
                getResources().getColor(R.color.colorRemoveFavorite) :
                getResources().getColor(R.color.colorAddFavorite));
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

        final String MOVIEDB_IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/";
        final String MOVIEDB_IMAGE_POSTER_SIZE = "w342";
        final String MOVIEDB_IMAGE_BACKDROP_SIZE = "w780";

        mTitleTextView.setText(mMovie.getTitle());

        Uri backdropImageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                .appendEncodedPath(MOVIEDB_IMAGE_BACKDROP_SIZE)
                .appendEncodedPath(mMovie.getBackdropPath())
                .build();
        Picasso.with(getActivity()).load(backdropImageLocation.toString()).into(mBackdropImageView);


        Uri imageLocation = Uri.parse(MOVIEDB_IMAGE_BASE_PATH).buildUpon()
                .appendEncodedPath(MOVIEDB_IMAGE_POSTER_SIZE)
                .appendEncodedPath(mMovie.getPosterPath())
                .build();
        Picasso.with(getActivity()).load(imageLocation.toString()).into(mPosterImageView);
        mOverviewTextView.setText(mMovie.getOverview());

        String voteAverageStr = Double.toString(mMovie.getVoteAverage()) + "/10";
        mVoteAverageTextView.setText(voteAverageStr);

        //SimpleDateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat releaseDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date releaseDate = mMovie.getReleaseDate();
        mReleaseDateTextView.setText(releaseDateFormat.format(releaseDate));

        mTrailersTextView.setText(R.string.trailers);

        mReviewsTextView.setText(R.string.reviews);
    }

    private void setShareTrailerIntent(MovieTrailer trailer) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mMovie.getTitle());

        if(trailer != null) {
            final String YOUTUBE_BASE_NAME = "https://www.youtube.com/watch?v=";
            shareIntent.putExtra(Intent.EXTRA_TEXT, trailer.getName() + ": " +
                    YOUTUBE_BASE_NAME + trailer.getKey());
        }

        if(mShareActionProvider != null)
            mShareActionProvider.setShareIntent(shareIntent);
        else
            Log.d(LOG_TAG, "ShareActionProvider is null?");
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
        private TextView mSeeMore;

        private MovieReview mReview;
        private int mPosition;

        public MovieReviewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTextAuthor = (TextView) itemView.findViewById(R.id.list_item_movie_review_author);
            mTextContent = (TextView) itemView.findViewById(R.id.list_item_movie_review_content);
            mSeeMore = (TextView) itemView.findViewById(R.id.list_item_see_more_overflow);
        }

        @Override
        public void onClick(View view) {
            Intent intent = MovieReviewListActivity
                    .newIntent(getActivity(), (ArrayList) mMovieReviews, mPosition);
            startActivity(intent);
        }

        public void bindMovieReview(MovieReview movieReview) {
            mReview = movieReview;

            mTextAuthor.setText(mReview.getReviewAuthor());
            mTextContent.setText(mReview.getReviewContent());

            // Set new height based on the text size to only show a single line of text initially
            setTextViewHeightToSingleLineHeight(mTextContent);
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        // Calculates width and height for a line of text and decides whether or not to display
        // additional text to "read more"
        private void setTextViewHeightToSingleLineHeight(TextView textView) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            // round up to make sure we don't lose pixels
            float adjustedTextSize = (textView.getTextSize() + metrics.density) / metrics.density;

            Typeface typeface = textView.getTypeface();

            float rowTextHeight = Utility.getTextHeight(
                    mReview.getReviewContent(),
                    adjustedTextSize,
                    typeface
            );
            // round up to make sure we don't lose pixels
            double calculatedTextHeight = Math.ceil((rowTextHeight + metrics.density) * metrics.density);
            textView.getLayoutParams().height = (int)calculatedTextHeight;

            float rowTextWidth = Utility.getTextWidth(mReview.getReviewContent(), adjustedTextSize, typeface);
            // convert back to actual size
            double calculatedTextWidth = Math.ceil((rowTextWidth + metrics.density) * metrics.density);

            if(calculatedTextWidth > metrics.widthPixels)
                mSeeMore.setVisibility(VISIBLE);
            else
                mSeeMore.setVisibility(GONE);
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
            holder.setPosition(position);
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
