package se.chalmers.ocuclass.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.widget.ConnectedUserHudView;

/**
 * Created by richard on 23/10/15.
 */
public class ConnectedUserHudFragment extends RxFragment {


    private static final String EXTRA_PRESENTATION_ID = "extra_presentation_id";
    private String presentationId;
    private ConnectedUserHudView connectedUsersHud;

    public static ConnectedUserHudFragment newInstance(String presentationId){

        ConnectedUserHudFragment fragment = new ConnectedUserHudFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_PRESENTATION_ID,presentationId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        presentationId = getArguments().getString(EXTRA_PRESENTATION_ID);

        RestClient.service().presentationViewers(presentationId).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).compose(this.<List<User>>bindToLifecycle()).subscribe(new Action1<List<User>>() {
            @Override
            public void call(List<User> users) {
                connectedUsersHud.setConnectedUsers(users);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                connectedUsersHud.error();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {




        connectedUsersHud = (ConnectedUserHudView) inflater.inflate(R.layout.view_hud_connected_users,container,false);


        return connectedUsersHud;
    }
}
