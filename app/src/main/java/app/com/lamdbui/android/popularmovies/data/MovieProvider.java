package app.com.lamdbui.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;

/**
 * Created by lamdbui on 1/20/17.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int MOVIES_POPULAR = 101;
    public static final int MOVIES_TOP_RATED = 102;
    public static final int MOVIES_FAVORITES = 103;

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIES_TOP_RATED:
            case MOVIES_FAVORITES:
            case MOVIES_POPULAR:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MOVIES:
                return MovieContract.MovieTable.CONTENT_TYPE;
            case MOVIES_POPULAR:
                return MovieContract.MovieTable.CONTENT_TYPE;
            case MOVIES_TOP_RATED:
                return MovieContract.MovieTable.CONTENT_TYPE;
            case MOVIES_FAVORITES:
                return MovieContract.MovieTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match) {
            case MOVIES:
                long _id = db.insert(MovieContract.MovieTable.TABLE_NAME, null, contentValues);
                if(_id > 0)
                    returnUri = MovieContract.MovieTable.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        // if(selection == null) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieTable.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.delete(
                        MovieContract.MovieTable.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add our URI paths
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIES + "/" + MovieContract.SUBPATH_POPULAR,
                MOVIES_POPULAR);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIES + "/" + MovieContract.SUBPATH_TOP_RATED,
                MOVIES_TOP_RATED);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIES + "/" + MovieContract.SUBPATH_FAVORITES,
                MOVIES_FAVORITES);

        return matcher;
    };
}
