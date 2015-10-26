package se.chalmers.ocuclass.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.ui.fragment.ConnectedUserHudFragment;
import se.chalmers.ocuclass.ui.fragment.InfoHudFragment;
import se.chalmers.ocuclass.ui.fragment.NotesHudFragment;

/**
 * Created by richard on 22/10/15.
 */
public class HudPagerAdapter extends FragmentPagerAdapter {

    private final String presentationId;
    private final String presentationName;
    private final boolean isTeacher;

    public HudPagerAdapter(FragmentManager fm, Presentation presentation, boolean isTeacher) {
        super(fm);
        this.presentationId = presentation.getId();
        this.presentationName = presentation.getName();
        this.isTeacher = isTeacher;
    }

    @Override
    public Fragment getItem(int position) {

        if(!isTeacher){
            return InfoHudFragment.newInstance(presentationId, presentationName);
        }

        int viewType = position%3;

        switch (viewType){
            case 0:
                return InfoHudFragment.newInstance(presentationId, presentationName);
            case 1:
                return ConnectedUserHudFragment.newInstance(presentationId);
            case 2:
                return NotesHudFragment.newInstance(presentationId);
        }
        return null;

    }


    @Override
    public int getCount() {

        return isTeacher?Integer.MAX_VALUE:1;
    }
}
