package se.chalmers.ocuclass.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import se.chalmers.ocuclass.net.RestClient;

/**
 * Created by richard on 15/10/15.
 */
public class RxFacebook {

    private CallbackManager callbackManager;

    public RxFacebook(Context context){
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


    public Observable<LoginResult> login(Fragment fragment,final LoginButton loginButton,String... permissions){
        loginButton.setFragment(fragment);
        return login(loginButton, permissions);
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public Observable<GraphResponseWrapper> graph(final AccessToken accessToken, int requestMethod){

        return Observable.create(new Observable.OnSubscribe<GraphResponseWrapper>() {
            @Override
            public void call(final Subscriber<? super GraphResponseWrapper> subscriber) {

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        null
                        /*,new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                if(!subscriber.isUnsubscribed()){
                                    subscriber.onNext(new GraphResponseWrapper(jsonObject,response));
                                }
                            }
                        }*/);

                GraphResponse response = request.executeAndWait();
                subscriber.onNext(new GraphResponseWrapper(response.getJSONObject(),response));
            }
        });


    }

    public Observable<LoginResult> login(final LoginButton loginButton,String... permissions){
        loginButton.setReadPermissions(permissions);

        return Observable.create(new Observable.OnSubscribe<LoginResult>() {
            @Override
            public void call(final Subscriber<? super LoginResult> subscriber) {
                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        //// FIXME: 15/10/15 this should not be forced to run as a thread
                        //ensure to run this as a thread
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(!subscriber.isUnsubscribed()){
                                    subscriber.onNext(loginResult);
                                    subscriber.onCompleted();
                                }
                            }
                        });
                        t.setPriority(Thread.NORM_PRIORITY-1);
                        t.start();

                    }

                    @Override
                    public void onCancel() {
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        subscriber.onError(exception);
                    }
                });
            }
        });
    };

    public static class GraphResponseWrapper {

        public JSONObject jsonObject;
        public GraphResponse response;

        public GraphResponseWrapper(JSONObject jsonObject, GraphResponse response) {
            this.jsonObject = jsonObject;
            this.response = response;
        }
    }
}
