package se.chalmers.ocuclass.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import se.chalmers.ocuclass.R;

/**
 * Created by richard on 25/10/15.
 */
public class OptionsDialogFragment extends AppCompatDialogFragment {


    public interface Callback{
        void onOptionSelected(int index);
    }


    private static final String EXTRA_OPTIONS = "extra_options";


    private String[] options;

    public static OptionsDialogFragment newInstance(String... options){

        OptionsDialogFragment fragment = new OptionsDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(EXTRA_OPTIONS,options);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        options = getArguments().getStringArray(EXTRA_OPTIONS);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_device)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Activity activity = getActivity();
                        if(activity instanceof Callback){
                            ((Callback)activity).onOptionSelected(which);
                        }
                    }
                });
        return builder.create();
    }
}
