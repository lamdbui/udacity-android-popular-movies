package app.com.lamdbui.android.popularmovies;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    private RecyclerView mMovieListRecyclerView;

    public MovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mMovieListRecyclerView = (RecyclerView) view.findViewById(R.id.movie_list_recycler_view);
        //mMovieListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMovieListRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return view;
    }

    private class MovieListHolder extends RecyclerView.ViewHolder {

        public MovieListHolder(View itemView) {
            super(itemView);
        }
    }

    private class MovieListAdapter extends RecyclerView.Adapter<MovieListHolder> {

        @Override
        public MovieListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MovieListHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
