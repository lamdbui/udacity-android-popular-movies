package app.com.lamdbui.android.popularmovies.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lamdbui on 1/23/17.
 */

public class MovieDbClient {

    private static final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/";

    private static Retrofit sRetrofit = null;

    public static Retrofit getClient() {
        if(sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(MOVIEDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
