package se.chalmers.ocuclass.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.ui.fragment.LoginFragment;
import se.chalmers.ocuclass.util.Utils;

/**
 * Created by richard on 24/09/15.
 */
public class LoginActivity extends AppCompatActivity {


    private static final String TAG_FRAGMENT = "tag_fragment";
    private LoginFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*ViewGroup.LayoutParams llp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout cntFragmentWrapper = new FrameLayout(this);
        cntFragmentWrapper.setLayoutParams(llp);
        cntFragmentWrapper.setId(android.R.id.content);*/


        setContentView(R.layout.activity_login);


        ViewGroup cntFragment = (ViewGroup) findViewById(R.id.cnt_fragment);



        FragmentManager fm = getSupportFragmentManager();

        if(savedInstanceState==null){
            fragment = LoginFragment.newInstance();
            fm.beginTransaction().add(R.id.cnt_fragment,fragment,TAG_FRAGMENT).commit();
        }else{
            fragment = (LoginFragment) fm.findFragmentByTag("tag_fragment");
        }

        cntFragment.setPadding(0,0,0, Utils.getNavigationBarHeight(this));






    }
}
