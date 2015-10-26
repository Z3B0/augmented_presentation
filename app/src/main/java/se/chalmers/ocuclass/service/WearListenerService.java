package se.chalmers.ocuclass.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import rx.functions.Func1;
import se.chalmers.ocuclass.model.PresentationEvent;
import se.chalmers.ocuclass.net.BaseResponse;
import se.chalmers.ocuclass.net.RestClient;


/**
 * Created by richard on 05/10/15.
 */
public class WearListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static String presentationId;

    private static final String TAG = WearListenerService.class.getSimpleName();
    private static final String PATH_WEAR_DIRECTION_EVENT = "/wear-direction-event";

    public static final String EVENT_WEAR_DIRECTION = "wear-event";
    public static final String EXTRA_DIRECTION = "extra_direction";

    private GoogleApiClient mGoogleApiClient;

    public final CountDownLatch latch = new CountDownLatch(1);


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(Bundle bundle) {
        latch.countDown();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();

    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(PATH_WEAR_DIRECTION_EVENT)) {


            try {
                if (latch.getCount() > 0) {
                    latch.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String direction = new String(messageEvent.getData());


            if (presentationId != null) {


                BaseResponse response = RestClient.service().postEvent(presentationId, new PresentationEvent(direction)).onErrorResumeNext(new Func1<Throwable, rx.Observable<? extends BaseResponse>>() {
                    @Override
                    public rx.Observable<? extends BaseResponse> call(Throwable throwable) {
                        throwable.printStackTrace();
                        return rx.Observable.just(null);
                    }
                }).toBlocking().first();

                Intent intent = new Intent(EVENT_WEAR_DIRECTION);
                intent.putExtra(EXTRA_DIRECTION, direction);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            }


        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
