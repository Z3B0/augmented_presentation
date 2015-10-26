package se.chalmers.ocuclass.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.widget.ConnectedUserHudView;

/**
 * Created by richard on 25/10/15.
 */
public class NotesHudFragment extends Fragment {
    public static Fragment newInstance(String presentationId) {

        NotesHudFragment fragment = new NotesHudFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.view_hud_notes,container,false);


        return root;
    }
}
