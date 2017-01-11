package app.com.lamdbui.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewListActivity extends SingleFragmentActivity {

    public static final String EXTRA_MOVIE_REVIEWS_PARCEL =
            "app.com.lamdbui.android.popularmovies.movie_review_parcel";

    public static Intent newIntent(Context packageContext, ArrayList<MovieReview> reviews) {
        Intent intent = new Intent(packageContext, MovieReviewListActivity.class);
        intent.putExtra(EXTRA_MOVIE_REVIEWS_PARCEL, reviews);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        ArrayList<MovieReview> reviews =
                getIntent().getParcelableArrayListExtra(EXTRA_MOVIE_REVIEWS_PARCEL);

        return MovieReviewListFragment.newInstance(reviews);
    }
}
