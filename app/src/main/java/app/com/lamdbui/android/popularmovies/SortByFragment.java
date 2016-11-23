package app.com.lamdbui.android.popularmovies;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;

/**
 * Created by lamdbui on 11/23/16.
 */

public class SortByFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort by")
                .setSingleChoiceItems(R.array.sort_by_options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Selected: " + which, Toast.LENGTH_SHORT);
                    }
                })
                .setPositiveButton("OKay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do stuff here
                    }
                });

        return builder.create();
    }
}
