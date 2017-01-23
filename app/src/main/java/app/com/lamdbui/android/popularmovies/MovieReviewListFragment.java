package app.com.lamdbui.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.lamdbui.android.popularmovies.model.MovieReview;

/**
 * Created by lamdbui on 1/10/17.
 */

public class MovieReviewListFragment extends Fragment {

    private static final String ARG_MOVIE_REVIEWS_PARCEL = "movie_reviews";
    private static final String ARG_MOVIE_REVIEWS_POSITION = "position";

    private RecyclerView mMovieReviewRecyclerView;
    private MovieReviewListAdapter mAdapter;

    private List<MovieReview> mMovieReviews;
    private int mPosition;

    public static MovieReviewListFragment newInstance(ArrayList<MovieReview> reviews,
                                                      int position) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MOVIE_REVIEWS_PARCEL, reviews);
        args.putInt(ARG_MOVIE_REVIEWS_POSITION, position);

        MovieReviewListFragment fragment = new MovieReviewListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mMovieReviews = args.getParcelableArrayList(ARG_MOVIE_REVIEWS_PARCEL);
        mPosition = args.getInt(ARG_MOVIE_REVIEWS_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_review_list, container, false);

        mAdapter = new MovieReviewListAdapter(mMovieReviews);
        mMovieReviewRecyclerView =
                (RecyclerView) view.findViewById(R.id.movie_review_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // scroll to the position we previously selected
        layoutManager.scrollToPosition(mPosition);
        mMovieReviewRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider =
                new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        mMovieReviewRecyclerView.addItemDecoration(divider);
        mMovieReviewRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private class MovieReviewListViewHolder extends RecyclerView.ViewHolder {

        private TextView mContent;
        private TextView mAuthor;

        private MovieReview mReview;

        public MovieReviewListViewHolder(View itemView) {
            super(itemView);

            mContent = (TextView) itemView.findViewById(R.id.movie_review_detail_content);
            mAuthor = (TextView) itemView.findViewById(R.id.movie_review_detail_author);
        }

        public void bindReview(MovieReview review) {
            mReview = review;

            mContent.setText(mReview.getReviewContent());
            mAuthor.setText(mReview.getReviewAuthor());
        }
    }

    private class MovieReviewListAdapter extends RecyclerView.Adapter<MovieReviewListViewHolder> {

        List<MovieReview> mReviews;

        public MovieReviewListAdapter(List<MovieReview> reviews) {
            super();

            mReviews = reviews;
        }

        @Override
        public MovieReviewListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_movie_review_detail, parent, false);

            return new MovieReviewListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieReviewListViewHolder holder, int position) {
            MovieReview review = mReviews.get(position);
            holder.bindReview(review);
        }

        @Override
        public int getItemCount() {
            return mReviews.size();
        }

        public void setReviews(List<MovieReview> reviews) {
            mReviews = reviews;
        }
    }
}
