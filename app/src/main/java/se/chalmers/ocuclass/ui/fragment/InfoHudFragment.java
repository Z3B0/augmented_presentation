package se.chalmers.ocuclass.ui.fragment;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.widget.InfoHudView;

/**
 * Created by richard on 23/10/15.
 */
public class InfoHudFragment extends Fragment {


    private static final String EXTRA_PRESENTATION_ID = "extra_presentation_id";
    private static final String EXTRA_PRESENTATION_NAME = "extra_presentation_name";

    private InfoHudView infoHudView;
    private String presentationId;
    private String presentationName;

    public static InfoHudFragment newInstance(String presentationId, String presentationName){

        //FIXME remove this hardcoded info:
        presentationName = "Neuroscience 101";

        InfoHudFragment fragment = new InfoHudFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_PRESENTATION_ID,presentationId);
        args.putString(EXTRA_PRESENTATION_NAME, presentationName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.presentationId = getArguments().getString(EXTRA_PRESENTATION_ID);
        this.presentationName = getArguments().getString(EXTRA_PRESENTATION_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        infoHudView = (InfoHudView) inflater.inflate(R.layout.view_hud_info,container,false);

        infoHudView.setPresentationName(presentationName);


        return infoHudView;
    }

    public void setInfo(String time, String elapsed) {

        infoHudView.setTime(time);
        infoHudView.setElapsedTime(elapsed);

    }
}
