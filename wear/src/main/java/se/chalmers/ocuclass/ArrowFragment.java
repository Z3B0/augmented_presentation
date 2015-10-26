package se.chalmers.ocuclass;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.CircularButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by richard on 13/10/15.
 */
public class ArrowFragment extends Fragment {

    private CircularButton btnRotate;
    private ImageView imgArrows;
    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_layout,container,false);

        btnRotate = (CircularButton)root.findViewById(R.id.btn_rotate);
        imgArrows = (ImageView)root.findViewById(R.id.img_arrows);

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).onClickRotate();
            }
        });



        return root;
    }

    public void onExitAmbient() {
        imgArrows.setImageResource(R.drawable.ic_arrows);
        root.setBackgroundResource(R.drawable.background);

        btnRotate.getBackground().clearColorFilter();
        //btnRotate.setColor(Color.GREEN);

    }

    public void onEnterAmbient() {
        root.setBackground(null);
        //btnRotate.setColor(Color.TRANSPARENT);
        btnRotate.getBackground().setColorFilter(0xFF222222, PorterDuff.Mode.MULTIPLY);
        imgArrows.setImageResource(R.drawable.ic_arrows_ambient);
    }
}
