package se.chalmers.ocuclass;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by richard on 13/10/15.
 */
public class ArrowFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageView view = new ImageView(getActivity());
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        view.setImageResource(R.drawable.ic_arrows);



        return view;
    }
}
