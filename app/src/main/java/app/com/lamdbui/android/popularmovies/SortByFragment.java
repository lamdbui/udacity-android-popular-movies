package app.com.lamdbui.android.popularmovies;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

/**
 * Created by lamdbui on 11/23/16.
 */

public class SortByFragment extends AppCompatDialogFragment {

    public static final String EXTRA_SORT_BY = "com.lamdbui.android.popularmovies.sort_by";

    private static final String ARG_SORT_BY = "sort_by";

    private static final String SAVED_SORT_BY = "sort_by";

    private MovieListFragment.SortBy mSelectedSortOption;

    // There is probably a better way to do this, as this requires knowledge of MovieListFragment
    public static SortByFragment newInstance(MovieListFragment.SortBy currSortOption) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SORT_BY, currSortOption);

        SortByFragment fragment = new SortByFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SORT_BY, mSelectedSortOption);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            mSelectedSortOption =(MovieListFragment.SortBy) savedInstanceState.getSerializable(SAVED_SORT_BY);
        }
        else {
            mSelectedSortOption = (MovieListFragment.SortBy)
                    getArguments().getSerializable(ARG_SORT_BY);
        }

        int sortByPosition = 0;

        switch(mSelectedSortOption) {
            case POPULAR:
                sortByPosition = 0;
                break;
            case TOP_RATED:
                sortByPosition = 1;
                break;
            default:
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.menu_item_sort_by)
                .setSingleChoiceItems(R.array.sort_by_options, sortByPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                mSelectedSortOption = MovieListFragment.SortBy.POPULAR;
                                break;
                            case 1:
                                mSelectedSortOption = MovieListFragment.SortBy.TOP_RATED;
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send our result back to our Target Fragment
                        sendResult(Activity.RESULT_OK, mSelectedSortOption);
                    }
                });

        return builder.create();
    }

    private void sendResult(int resultCode, MovieListFragment.SortBy sortBy) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SORT_BY, sortBy);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
