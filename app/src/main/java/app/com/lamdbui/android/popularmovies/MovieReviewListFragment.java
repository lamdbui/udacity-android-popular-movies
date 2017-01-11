package app.com.lamdbui.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lamdbui on 1/10/17.
 */

public class MovieReviewListFragment extends Fragment {

    private RecyclerView mMovieReviewRecyclerView;
    private MovieReviewListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_review_list, container, false);

        mAdapter = new MovieReviewListAdapter();
        mMovieReviewRecyclerView =
                (RecyclerView) view.findViewById(R.id.movie_review_list_recycler_view);
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

        @Override
        public MovieReviewListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
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
