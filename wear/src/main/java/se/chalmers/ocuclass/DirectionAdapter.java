package se.chalmers.ocuclass;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by richard on 13/10/15.
 */
public class DirectionAdapter extends FragmentGridPagerAdapter {

    public DirectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getFragment(int i, int i1) {
        return new ArrowFragment();
    }

    @Override
    public int getRowCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getColumnCount(int i) {
        return Integer.MAX_VALUE;
    }



}
