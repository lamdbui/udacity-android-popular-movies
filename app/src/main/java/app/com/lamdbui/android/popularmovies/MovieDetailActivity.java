package app.com.lamdbui.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import app.com.lamdbui.android.popularmovies.model.Movie;

public class MovieDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_MOVIE_PARCEL =
            "com.lamdbui.android.popularmovies.movie_parcel";

    public static Intent newIntent(Context packageContext, Movie movie) {
        Intent intent = new Intent(packageContext, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE_PARCEL, movie);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Movie movie = (Movie) getIntent().getParcelableExtra(EXTRA_MOVIE_PARCEL);
        return MovieDetailFragment.newInstance(movie);
    }
}
