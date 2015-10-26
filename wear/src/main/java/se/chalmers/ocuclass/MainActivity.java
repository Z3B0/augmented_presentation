package se.chalmers.ocuclass;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.GridViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int FLING_SENSITIVITY = 50;
    private static final String EVENT_RIGHT = "right";
    private static final String EVENT_LEFT = "left";
    private static final String EVENT_UP = "up";
    private static final String EVENT_DOWN = "down";
    private static final String EVENT_ROTATE = "rotate";
    private static final String PATH_WEAR_DIRECTION_EVENT = "/wear-direction-event";

    private DismissOverlayView dismissOverlay;
    private GestureDetector mDetector;
    private GoogleApiClient mGoogleApiClient;
    private DirectionAdapter adapter;
    private GridViewPager gridViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAmbientEnabled();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        adapter = new DirectionAdapter(getFragmentManager());

        gridViewPager = (GridViewPager) findViewById(R.id.grid_view_pager);
        gridViewPager.setAdapter(adapter);

        gridViewPager.setCurrentItem(Integer.MAX_VALUE/2,Integer.MAX_VALUE/2);
        adapter.notifyDataSetChanged();

        gridViewPager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {

            private int lastCol = 0;
            private int lastRow = 0;


            @Override
            public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {

            }

            @Override
            public void onPageSelected(int row, int col) {





                if(col > lastCol){
                    sendEventToApp(EVENT_RIGHT);
                }
                else if(col < lastCol){
                    sendEventToApp(EVENT_LEFT);
                }else if(row > lastRow){
                    sendEventToApp(EVENT_DOWN);
                }
                else if(col < lastRow){
                    sendEventToApp(EVENT_UP);
                }

                lastRow = row;
                lastCol = col;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });








       /*gridViewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                             @Override
                                             public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {


                                                 gridViewPager.removeOnLayoutChangeListener(this);
                                             }

        });*/

        dismissOverlay = (DismissOverlayView)findViewById(R.id.dismiss_overlay);
        dismissOverlay.setIntroText(R.string.long_press_intro);
        dismissOverlay.showIntroIfNecessary();

        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                dismissOverlay.show();
            }

            /*@Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {



                if((e1.getX() - e2.getX()) > FLING_SENSITIVITY){
                    Toast.makeText(MainActivity.this,
                            "Previous", Toast.LENGTH_SHORT).show();

                    sendEventToApp(EVENT_LEFT);

                }else if((e2.getX() - e1.getX()) > FLING_SENSITIVITY){
                    Toast.makeText(MainActivity.this,
                            "Next", Toast.LENGTH_SHORT).show();
                    sendEventToApp(EVENT_RIGHT);
                }

                return true;
            }*/
        });

    }

    private Fragment findFragmentInPager(GridViewPager pager, int row, int column)
    {

        long itemId = adapter.getFragmentId(row, column);
        return getFragmentManager().findFragmentByTag( "android:switcher:" + pager.getId() + ":" + itemId);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);



        ((ArrowFragment)findFragmentInPager(gridViewPager,gridViewPager.getCurrentItem().y,gridViewPager.getCurrentItem().x)).onEnterAmbient();

    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        ((ArrowFragment)findFragmentInPager(gridViewPager,gridViewPager.getCurrentItem().y,gridViewPager.getCurrentItem().x)).onExitAmbient();

        /*
        mStateTextView.setTextColor(Color.GREEN);
        mStateTextView.getPaint().setAntiAlias(true);*/
    }



    private String pickBestNodeId(List<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private void sendEventToApp(final String type) {


        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                String nodeId = pickBestNodeId(nodes.getNodes());
                Wearable.MessageApi.sendMessage(
                        mGoogleApiClient, nodeId, PATH_WEAR_DIRECTION_EVENT, type.getBytes(Charset.forName("UTF-8"))).setResultCallback(
                        new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                if (!sendMessageResult.getStatus().isSuccess()) {
                                    showErrorMessage();
                                }
                            }
                        }
                );
            }
        });

    }

    private void showErrorMessage() {
        Toast.makeText(this, "Unable to send gesture", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();


    }


    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event) ||super.dispatchTouchEvent(event);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onClickRotate() {
        sendEventToApp(EVENT_ROTATE);
    }


}
