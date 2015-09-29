package se.chalmers.agumentedexploration.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import se.chalmers.agumentedexploration.R;
import se.chalmers.agumentedexploration.model.User;
import se.chalmers.agumentedexploration.ui.RenderActivity;
import se.chalmers.agumentedexploration.ui.UnityActivity;
import se.chalmers.augmentedexploration.unity.UnityPlayerActivity;

/**
 * Created by richard on 24/09/15.
 */
public class LoginFragment extends RxFragment {
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


                String json = getJson();


                UnityActivity.startActivity(getContext(),json);

                //Intent newIntent = new Intent(getContext(), UnityPlayerActivity.class);
                //getContext().startActivity(newIntent);

                //RenderActivity.startActivity(getContext(),new User());
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private String getJson() {
        //FIXME remove this shit and fix network

        String result = "";

        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getContext().getAssets().open("presentation.json");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
            result = buf.toString();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return result;
    }
}
