package app.com.lamdbui.android.popularmovies.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import app.com.lamdbui.android.popularmovies.data.MovieContract.MovieTable;

/**
 * Created by lamdbui on 1/19/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

//        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
//                LocationEntry._ID + " INTEGER PRIMARY KEY," +
//                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
//                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
//                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
//                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL " +
//                " );";

//        public static final String POPULAR = "popular";
//        public static final String TOP_RATED = "top_rated";
//        public static final String FAVORITE = "favorite";

//        public static final String ID = "id";
//        public static final String RELEASE_DATE = "release_date";
//        //public static final String GENRE_IDS = "genre_ids";
//        public static final String TITLE = "title";
//        public static final String ORIGINAL_TITLE = "original_title";
//        public static final String ORIGINAL_LANGUAGE = "original_langauge";
//        public static final String POPULARITY = "popularity";
//        public static final String VOTE_COUNT = "vote_count";
//        public static final String VIDEO = "video";
//        public static final String VOTE_AVERAGE = "vote_average";
//        public static final String POSTER_PATH = "poster_path";
//        public static final String BACKDROP_PATH = "backdrop_path";
//        public static final String OVERVIEW = "overview";
//        public static final String ADULT = "adult";

        // Create the table for the Movies
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieTable.TABLE_NAME + " (" +
                MovieTable._ID + " INTEGER PRIMARY KEY," +
                MovieTable.COLS.ID + " REAL UNIQUE NOT NULL, " +
                MovieTable.COLS.RELEASE_DATE + " TEXT NOT NULL, " +
                MovieTable.COLS.ORIGINAL_TITLE + " TEXT, " +
                MovieTable.COLS.ORIGINAL_LANGUAGE + " TEXT, " +
                MovieTable.COLS.POPULARITY + " TEXT, " +
                MovieTable.COLS.VOTE_COUNT + " REAL NOT NULL, " +
                MovieTable.COLS.VIDEO + " TEXT NOT NULL, " +
                MovieTable.COLS.VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieTable.COLS.POSTER_PATH + " TEXT NOT NULL, " +
                MovieTable.COLS.BACKDROP_PATH + " TEXT, " +
                MovieTable.COLS.OVERVIEW + " TEXT NOT NULL, " +
                MovieTable.COLS.ADULT + " TEXT, " +

                // add flags to associate lists
                MovieTable.COLS.POPULAR + " INTEGER, " +
                MovieTable.COLS.TOP_RATED + " INTEGER, " +
                MovieTable.COLS.FAVORITE + " INTEGER " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // drop and recreate the tables for now.
        // we probably want to do something better in the future...
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieTable.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
