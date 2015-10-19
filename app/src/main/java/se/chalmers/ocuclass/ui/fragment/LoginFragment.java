package se.chalmers.ocuclass.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.PresentationListResponse;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.ui.MainActivity;
import se.chalmers.ocuclass.ui.NfcDetectActivity;
import se.chalmers.ocuclass.util.RxFacebook;

/**
 * Created by richard on 24/09/15.
 */
public class LoginFragment extends RxFragment {
    private static final String DIALOG_TAG_PROGRESS = "dialog_tag_progress";
    private static final String TAG = LoginFragment.class.getSimpleName();
    private Button btnLogin;
    private EditText txtUserName;
    private EditText txtPassword;


    private ProgressDialogFragment progressDialogFragment;
    private RxFacebook rxFacebook;

    private String json;
    private User user;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    public String progressMessage;


    private Runnable updateProgressbarRunnable = new Runnable() {
        @Override
        public void run() {
            if(progressDialogFragment.isHidden()) {
                progressDialogFragment.show(getFragmentManager(), DIALOG_TAG_PROGRESS);
            }
            progressDialogFragment.setMessage(progressMessage);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);


        btnLogin = (Button) rootView.findViewById(R.id.btn_login);
        txtUserName = (EditText)rootView.findViewById(R.id.txt_username);
        txtPassword = (EditText)rootView.findViewById(R.id.txt_password);

        progressDialogFragment = ProgressDialogFragment.newInstance(getString(R.string.signing_in));


        txtUserName.setText("a@a.se");

        rxFacebook.login(this, (LoginButton) rootView.findViewById(R.id.login_button), "public_profile").flatMap(new Func1<LoginResult, Observable<RxFacebook.GraphResponseWrapper>>() {
            @Override
            public Observable<RxFacebook.GraphResponseWrapper> call(LoginResult loginResult) {


                return rxFacebook.graph(loginResult.getAccessToken(),0);
            }
        }).flatMap(new Func1<RxFacebook.GraphResponseWrapper, Observable<User>>() {
            @Override
            public Observable<User> call(RxFacebook.GraphResponseWrapper graphResponseWrapper) {

                String name = null;
                String id = null;
                //String email = null;

                try {
                    name = graphResponseWrapper.jsonObject.getString("name");
                    id = graphResponseWrapper.jsonObject.getString("id");

                } catch (JSONException e) {
                    return Observable.error(e);
                }




                updateProgressbar(getString(R.string.signing_in));


                return RestClient.service().register(new User(id, name, "facebook"));
            }
        }).flatMap(new Func1<User, Observable<PresentationListResponse>>() {


            @Override
            public Observable<PresentationListResponse> call(User user) {
                updateProgressbar(getString(R.string.loading_presentation));
                RestClient.getInstance().setUser(user, user.getUserId());
                LoginFragment.this.user = user;
                return RestClient.service().presentationList();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).compose(LoginFragment.this.<PresentationListResponse>bindToLifecycle()).subscribe(new Action1<PresentationListResponse>() {
            @Override
            public void call(PresentationListResponse presentationList) {

                onComplete(presentationList.getPresentations().get(0));
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                loginError(getString(R.string.err_login));
            }
        });


                /*.observeOn(Schedulers.newThread()).subscribeOn(AndroidSchedulers.mainThread()).compose(LoginFragment.this.<RxFacebook.GraphResponseWrapper>bindToLifecycle()).subscribe(new Action1<RxFacebook.GraphResponseWrapper>() {
            @Override
            public void call(RxFacebook.GraphResponseWrapper graphResponseWrapper) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                loginError(getString(R.string.err_login));
            }
        });*/



        //



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                progressDialogFragment.show(getFragmentManager(), DIALOG_TAG_PROGRESS);



                final String username = txtUserName.getText().toString();
                final String password = txtPassword.getText().toString();

                updateProgressbar(getString(R.string.signing_in));

                RestClient.service().login(User.password(username, password)).flatMap(new Func1<User, Observable<PresentationListResponse>>() {


                    @Override
                    public Observable<PresentationListResponse> call(User user) {
                        updateProgressbar(getString(R.string.loading_presentation));
                        RestClient.getInstance().setUser(user, password);
                        LoginFragment.this.user = user;
                        return RestClient.service().presentationList();
                    }
                }).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).compose(LoginFragment.this.<PresentationListResponse>bindToLifecycle()).subscribe(new Action1<PresentationListResponse>() {


                            @Override
                            public void call(PresentationListResponse presentationList) {
                                onComplete(presentationList.getPresentations().get(0));
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();

                                loginError(getString(R.string.err_login));

                            }
                });


                //Intent newIntent = new Intent(getContext(), UnityPlayerActivity.class);
                //getContext().startActivity(newIntent);

            }
        });

        return rootView;
    }

    private void updateProgressbar(String string) {
        progressMessage = getString(R.string.signing_in);
        getActivity().runOnUiThread(updateProgressbarRunnable);
    }

    private void onComplete(Presentation presentation) {

        if(progressDialogFragment!=null) {
            progressDialogFragment.dismiss();
        }

        if (user.getUserType() == User.UserType.TEACHER) {
            MainActivity.startActivity(getContext(), user,presentation);
        } else {
            NfcDetectActivity.startActivity(getContext(), user,presentation);
        }
    }

    private void loginError(String errorText) {
        if(progressDialogFragment!=null) {
            progressDialogFragment.dismiss();
        }
        Toast.makeText(getActivity(),errorText , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        json = getJson();
        rxFacebook = new RxFacebook(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        rxFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
