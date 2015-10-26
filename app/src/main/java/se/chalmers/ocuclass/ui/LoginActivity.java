package se.chalmers.ocuclass.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.ui.fragment.ConnectedUserHudFragment;
import se.chalmers.ocuclass.ui.fragment.LoginFragment;
import se.chalmers.ocuclass.util.Utils;

/**
 * Created by richard on 24/09/15.
 */
public class LoginActivity extends AppCompatActivity {


    private static final String TAG_FRAGMENT = "tag_fragment";
    private Fragment fragment;

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
            //RestClient.getInstance().setUser(new User("a@a.se","Test",null),"a");
            //fm.beginTransaction().add(R.id.cnt_fragment, ConnectedUserHudFragment.newInstance("56260a45cb099a1100e8449a")).commit();
        }else{
            fragment = (LoginFragment) fm.findFragmentByTag("tag_fragment");
        }






        //FIXME RESTORE
        //cntFragment.setPadding(0,0,0, Utils.getNavigationBarHeight(this));






    }
}
