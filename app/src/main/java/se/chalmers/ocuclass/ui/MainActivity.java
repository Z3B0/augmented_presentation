package se.chalmers.ocuclass.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.io.Serializable;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.ui.fragment.MainFragment;

/**
 * Created by richard on 01/10/15.
 */
public class MainActivity extends ToolbarActivity {

    private static final String TAG_FRAGMENT = "tag_fragment";
    private static final String EXTRA_PRESENTATION = "extra_presentation";
    private static final String EXTRA_USER = "extra_user";
    private MainFragment fragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getToolbar().setTitle(getString(R.string.presentations));

        FragmentManager fm = getSupportFragmentManager();

        if(savedInstanceState==null){
            fragment = MainFragment.newInstance();
            fm.beginTransaction().add(R.id.cnt_fragment,fragment,TAG_FRAGMENT).commit();
        }else{
            fragment = (MainFragment) fm.findFragmentByTag("tag_fragment");
        }



    }

    public static void startActivity(Context context, User user, Presentation presentation) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_PRESENTATION, presentation);
        intent.putExtra(EXTRA_USER,user);
        context.startActivity(intent);

    }
}
