package se.chalmers.ocuclass.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;
import java.util.List;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.ui.fragment.MainFragment;

/**
 * Created by richard on 01/10/15.
 */
public class MainActivity extends ToolbarActivity {

    private static final String TAG_FRAGMENT = "tag_fragment";
    private static final String EXTRA_USER = "extra_user";
    private MainFragment fragment;
    private User user;
    private List<Presentation> presentations;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getToolbar().setTitle(getString(R.string.presentations));

        this.user = (User) getIntent().getExtras().getSerializable(EXTRA_USER);

        FragmentManager fm = getSupportFragmentManager();

        if(savedInstanceState==null){
            fragment = MainFragment.newInstance(user);
            fm.beginTransaction().add(R.id.cnt_fragment,fragment,TAG_FRAGMENT).commit();
        }else{
            fragment = (MainFragment) fm.findFragmentByTag("tag_fragment");
        }



    }

    public static void startActivity(Context context, User user) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_USER, user);
        context.startActivity(intent);

    }
}
