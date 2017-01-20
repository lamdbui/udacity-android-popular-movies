package app.com.lamdbui.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lamdbui on 1/19/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "app.com.lamdbui.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // possible paths
    public static final String PATH_MOVIES = "movies";

    // possible subpaths
    public static final String SUBPATH_POPULAR = "popular";
    public static final String SUBPATH_TOP_RATED = "top_rated";
    public static final String SUBPATH_FAVORITES = "favorites";

    // class that defines the Movie Table Schema
    public static final class MovieTable implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final class COLS {

            // extra field to identify associated list
            public static final String POPULAR = "popular";
            public static final String TOP_RATED = "top_rated";
            public static final String FAVORITE = "favorite";

            // from the Movie class
            public static final String ID = "id";
            public static final String RELEASE_DATE = "release_date";
            //public static final String GENRE_IDS = "genre_ids";
            public static final String TITLE = "title";
            public static final String ORIGINAL_TITLE = "original_title";
            public static final String ORIGINAL_LANGUAGE = "original_langauge";
            public static final String POPULARITY = "popularity";
            public static final String VOTE_COUNT = "vote_count";
            public static final String VIDEO = "video";
            public static final String VOTE_AVERAGE = "vote_average";
            public static final String POSTER_PATH = "poster_path";
            public static final String BACKDROP_PATH = "backdrop_path";
            public static final String OVERVIEW = "overview";
            public static final String ADULT = "adult";
        }

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
