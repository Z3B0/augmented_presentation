package se.chalmers.agumentedexploration.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import se.chalmers.agumentedexploration.R;
import se.chalmers.agumentedexploration.model.User;
import se.chalmers.agumentedexploration.ui.RenderActivity;

/**
 * Created by richard on 24/09/15.
 */
public class LoginFragment extends Fragment {
    private Button btnLogin;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);


        btnLogin = (Button) rootView.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RenderActivity.startActivity(getContext(),new User());
            }
        });

        return rootView;
    }
}
