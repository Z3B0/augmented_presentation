package se.chalmers.ocuclass.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.unity3d.player.UnityPlayer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.PresentationEvent;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.BaseResponse;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.service.WearListenerService;
import se.chalmers.ocuclass.widget.HudView;

/**
 * Created by richard on 29/09/15.
 */
public class UnityActivity extends RxAppCompatActivity {


    private String TAG = UnityActivity.class.getSimpleName();
    private TextView txtTimeCurrent;
    private TextView txtTimeElapsed;

    private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    private static final String EXTRA_PRESENTATION = "extra_presentation";
    private static final String UNITY_CLASS_PRESENTATION_HANDLER = "PresentationHandler";
    private static final String UNITY_METHOD_SETUP = "Setup";
    private static final String UNITY_METHOD_LERP = "StartLerping";
    private static final String EXTRA_USER = "extra_user";

    protected UnityPlayer mUnityPlayer;
    private User user;
    private Presentation presentation;

    public static class RetryWithDelay implements
            Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;
        private final int retryDelayMillis;
        private int retryCount;

        public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
            this.retryCount = 0;
        }

        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            if (++retryCount < maxRetries) {
                                // When this Observable calls onNext, the original
                                // Observable will be retried (i.e. re-subscribed).
                                return Observable.timer(retryDelayMillis,
                                        TimeUnit.MILLISECONDS);
                            }

                            // Max retries hit. Just pass the error along.
                            return Observable.error(throwable);
                        }
                    });
        }
    }


    public static void startActivity(Context context,User user,Presentation presentation) {
        Intent intent = new Intent(context, UnityActivity.class);
        intent.putExtra(EXTRA_PRESENTATION, presentation);
        intent.putExtra(EXTRA_USER, user);
        context.startActivity(intent);
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String direction = intent.getStringExtra(WearListenerService.EVENT_WEAR_DIRECTION);

            //Toast.makeText(UnityActivity.this, direction, Toast.LENGTH_LONG).show();

            mUnityPlayer.UnitySendMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_LERP, direction);

        }
    };



    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        presentation = (Presentation)getIntent().getExtras().getSerializable(EXTRA_PRESENTATION);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy


        setContentView(R.layout.activity_unity);


        FrameLayout cntPlayer = (FrameLayout) findViewById(R.id.cnt_player);

        txtTimeCurrent = (TextView)findViewById(R.id.txt_time_current);
        txtTimeElapsed = (TextView)findViewById(R.id.txt_time_elapsed);


        mUnityPlayer = new UnityPlayer(this);
        cntPlayer.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();


        //checkForEvents();


        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(WearListenerService.EVENT_WEAR_DIRECTION));


        mUnityPlayer.UnitySendMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_SETUP, new Gson().toJson(presentation));
    }


    private void checkForEvents() {

        //Log.d(TAG, "calling check for events...");

        RestClient.service().getEvent("unused")
                .retryWhen(new UnityActivity.RetryWithDelay(100,2000))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<PresentationEvent>bindToLifecycle()).subscribe(new Action1<PresentationEvent>() {
            @Override
            public void call(PresentationEvent presentationEvent) {


                mUnityPlayer.UnitySendMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_LERP, presentationEvent.getEvent());


                checkForEvents();
                //Log.d(TAG, "event found!" + presentationEvent.getEvent());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //Log.d(TAG, "timeout reached");
                checkForEvents();
            }
        });
    }



    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
        if(user.getUserType()== User.UserType.STUDENT){
            checkForEvents();
        }

        updateHud();
    }

    private void updateHud() {
        Observable.interval(1, TimeUnit.MINUTES).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).compose(this.<Long>bindToLifecycle()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long secondsElapsed) {

                setTime(secondsElapsed);
            }
        });
    }

    public void setLastSlideIndex(String index){

    }

    private void setTime(Long secondsElapsed) {
        int hours = (int) (secondsElapsed/60);
        int minutes = (int)(secondsElapsed%60);

        txtTimeElapsed.setText((hours>0?hours+"h ":"")+minutes+"m");

        txtTimeCurrent.setText(DATE_FORMAT_TIME.format(Calendar.getInstance().getTime()));
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }
}
