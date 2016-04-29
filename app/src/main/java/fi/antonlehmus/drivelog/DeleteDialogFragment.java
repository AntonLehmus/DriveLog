package fi.antonlehmus.drivelog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.SQLite;

public class DeleteDialogFragment extends DialogFragment {



    private EditText mEditText;



    public DeleteDialogFragment() {
    }



    public static DeleteDialogFragment newInstance(String title,long odoStart, long odoStop) {
        DeleteDialogFragment frag = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putLong("odoStart", odoStart);
        args.putLong("odoStop", odoStop);
        frag.setArguments(args);

        return frag;
    }



    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString("title");
        final Long odoStart = getArguments().getLong("odoStart");
        final Long odoStop = getArguments().getLong("odoStop");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(R.string.deleteDialogConfirm);
        alertDialogBuilder.setPositiveButton(R.string.delete,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SQLite.delete(Journey.class)
                        .where(Journey_Table.odometerStart.is(odoStart))
                        .and(Journey_Table.odometerStop.is(odoStop))
                        .async()
                        .execute();
            }

        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });

        return alertDialogBuilder.create();

    }
}