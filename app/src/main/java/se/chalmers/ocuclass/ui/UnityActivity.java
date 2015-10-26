package se.chalmers.ocuclass.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.unity3d.player.UnityPlayer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.adapters.HudPagerAdapter;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.PresentationEvent;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.service.WearListenerService;
import se.chalmers.ocuclass.ui.fragment.ConnectedUserHudFragment;
import se.chalmers.ocuclass.ui.fragment.InfoHudFragment;
import se.chalmers.ocuclass.widget.InfoHudView;
import se.chalmers.ocuclass.widget.VerticalViewPager;

/**
 * Created by richard on 29/09/15.
 */
public class UnityActivity extends RxAppCompatActivity {


    public static final String MARKER_CAT = "94CE6B04";
    public static final String MARKER_DOG = "C65E4B7A";
    public static final String MARKER_SNAIL = "56426D10";


    private String TAG = UnityActivity.class.getSimpleName();

    private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("HH:mm");
    private static final String EXTRA_PRESENTATION = "extra_presentation";
    private static final String UNITY_CLASS_PRESENTATION_HANDLER = "PresentationHandler";
    private static final String UNITY_METHOD_SETUP = "Setup";
    private static final String UNITY_METHOD_ROTATE = "RotateModel";
    private static final String UNITY_METHOD_LERP = "StartLerping";
    private static final String EXTRA_USER = "extra_user";

    protected UnityPlayer mUnityPlayer;
    private User user;
    private Presentation presentation;
    private long lastbackPressed = 0;
    private InfoHudFragment infoHudFragment;
    private VerticalViewPager pager;

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
            String direction = intent.getStringExtra(WearListenerService.EXTRA_DIRECTION);


            if (handleEvent(direction)) return;

            if(direction.equals("up")||direction.equals("down")){
                pager.setCurrentItem(pager.getCurrentItem()+(direction.equals("up")?-1:1),true);
                return;
            }



        }
    };

    private boolean handleEvent(String direction) {
        if(direction.equals("left")||direction.equals("right")){
            sendUnityMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_LERP, direction);

            return true;
        }

        if(direction.equals("rotate")){
            sendUnityMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_ROTATE, direction);
            return true;
        }
        return false;
    }

    private void sendUnityMessage(String unityClass, String unityMethod, String unityData) {
        try{
            UnityPlayer.UnitySendMessage(unityClass,unityMethod,unityData);
        }catch (Exception ex){
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
            ex.printStackTrace();
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
            Log.e(TAG,"=======================ERRROR================");
        }
    }



    public static class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
        private final String LINE_SEPARATOR = "\n";
        public static final String LOG_TAG = ExceptionHandler.class.getSimpleName();

        @SuppressWarnings("deprecation")
        public void uncaughtException(Thread thread, Throwable exception) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));

            StringBuilder errorReport = new StringBuilder();
            errorReport.append(stackTrace.toString());

            Log.e(LOG_TAG, errorReport.toString());

            exception.printStackTrace();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }



    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy


        setContentView(R.layout.activity_unity);



        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        presentation = (Presentation)getIntent().getExtras().getSerializable(EXTRA_PRESENTATION);
        user = (User)getIntent().getExtras().getSerializable(EXTRA_USER);

        WearListenerService.presentationId = presentation.getId();




        FrameLayout cntPlayer = (FrameLayout) findViewById(R.id.cnt_player);

        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int statusBarHeight = 0;
        if (resourceId > 0) {
            statusBarHeight =  resources.getDimensionPixelSize(resourceId);
        }

        boolean isTeacher = user.getUserType()== User.UserType.TEACHER;

        pager = (VerticalViewPager)findViewById(R.id.viewpager);

        pager.setAdapter(new HudPagerAdapter(getSupportFragmentManager(), presentation, isTeacher));

        if(isTeacher) {
            pager.setCurrentItem(Integer.MAX_VALUE / 2, false);
        }

        //stereoView.setPadding(stereoView.getPaddingLeft(),stereoView.getPaddingTop(),stereoView.getPaddingBottom(),stereoView.getPaddingRight()+statusBarHeight);





        //FIXME bring back
        mUnityPlayer = new UnityPlayer(this);
        cntPlayer.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();




        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(WearListenerService.EVENT_WEAR_DIRECTION));


        //mUnityPlayer.UnitySendMessage(UNITY_CLASS_PRESENTATION_HANDLER, UNITY_METHOD_SETUP, new Gson().toJson(presentation));
    }


    private void checkForEvents() {


        RestClient.service().getEvent(presentation.getId())
                .retryWhen(new UnityActivity.RetryWithDelay(100, 2000))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<PresentationEvent>bindToLifecycle()).subscribe(new Action1<PresentationEvent>() {
            @Override
            public void call(PresentationEvent presentationEvent) {

                handleEvent(presentationEvent.getEvent());

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
        Observable.interval(5,60, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).compose(this.<Long>bindToLifecycle()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long secondsElapsed) {

                setTime(secondsElapsed);
            }
        });
    }

    public void setLastSlideIndex(String index){

        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
        Log.d(TAG,"======= HERE??!?!?!");
    }

    private void setTime(Long secondsElapsed) {
        int hours = (int) (secondsElapsed/60);
        int minutes = (int)(secondsElapsed%60);

        //pager.findFragmentByClassName()

        infoHudFragment.setInfo(DATE_FORMAT_TIME.format(Calendar.getInstance().getTime()), (hours > 0 ? hours + "h " : "") + minutes+"m");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {

        if(fragment instanceof InfoHudFragment){
            this.infoHudFragment = (InfoHudFragment) fragment;
        }


        super.onAttachFragment(fragment);
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



    @Override
    public void onBackPressed() {


        if(System.currentTimeMillis()-lastbackPressed<=1000){
            finish();
        }else{
            Toast.makeText(this, R.string.press_again_to_confirm, Toast.LENGTH_SHORT).show();
        }

        lastbackPressed = System.currentTimeMillis();
    }

    /*

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


    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }*/
}
