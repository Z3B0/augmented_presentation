/*
 *  ARSimple.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package se.chalmers.agumentedexploration.ui;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import se.chalmers.agumentedexploration.R;
import se.chalmers.agumentedexploration.model.User;
import se.chalmers.agumentedexploration.rendering.SimpleRenderer;
import se.chalmers.agumentedexploration.rendering.MultiThreadRender;


/**
 * A very simple example of extending ARActivity to create a new AR application.
 */
public class RenderActivity extends RxAppCompatActivity implements SimpleRenderer.Callback {


    protected final static String TAG = RenderActivity.class.getSimpleName();
    private static final String EXTRA_USER = "extra_user";


    private CaptureCameraPreview preview;
    private GLSurfaceView glView;
    protected ARRenderer renderer;
    private ARToolKit toolKit = new ARToolKit();
    private boolean firstUpdate = false;
    private SimpleRenderer renderer1;
    private FrameLayout cntRenderer;
    private TextView textView;
    private View mDecorView;
    private int cameraWidth;
    private int cameraHeight;
    private int screenWidth;
    private int screenHeight;
    private MultiThreadRender.RenderThread renderThread;


    public static void startActivity(Context context, User user){

        Intent intent = new Intent(context,RenderActivity.class);
        intent.putExtra(EXTRA_USER,user);

        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mDecorView = getWindow().getDecorView();
        hideSystemUI();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        cntRenderer = (FrameLayout) findViewById(R.id.cnt_render);

        renderer = new SimpleRenderer(toolKit, this);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.cnt_root);

        textView = new TextView(this);
        textView.setText("Test");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        textView.setTextColor(Color.WHITE);

        textView.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        frameLayout.addView(textView, llp);

        if (toolKit.initialiseNative(this.getCacheDir().getAbsolutePath()) == false) { // Use cache directory for Data files.

            new AlertDialog.Builder(this)
                    .setMessage("The native library is not loaded. The application cannot continue.")
                    .setTitle("Error")
                    .setCancelable(true)
                    .setNeutralButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            })
                    .show();

            return;
        }


    }


    @Override
    public void onResume() {
        super.onResume();



        preview = new CaptureCameraPreview(RenderActivity.this, new CameraEventListener() {
            @Override
            public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
                cameraWidth = width;
                cameraHeight = height;

                if (toolKit.initialiseAR(width, height, "Data/camera_para.dat", cameraIndex, cameraIsFrontFacing)) { // Expects Data to be already in the cache dir. This can be done with the AssetUnpacker.
                    Log.i(TAG, "Camera initialised");
                } else {
                }

                firstUpdate = true;
            }

            @Override
            public void cameraPreviewFrame(byte[] frame) {
                if (firstUpdate) {
                    // ARToolKit has been initialised. The renderer can now add markers, etc...
                    if (renderer.configureARScene()) {
                        Log.i(TAG, "Scene configured successfully");
                    } else {
                        // Error
                        Log.e(TAG, "Error configuring scene. Cannot continue.");
                    }
                    firstUpdate = false;
                }
                if(renderThread!=null) {
                    renderThread.handleFrame(frame);
                }
            }

            @Override
            public void cameraPreviewStopped() {
                toolKit.cleanup();
            }
        });

        glView = new GLSurfaceView(this);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT); // Needs to be a translucent surface so the camera preview shows through.
        glView.setRenderer(renderer);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // Only render when we have a frame (must call requestRender()).
        glView.setZOrderMediaOverlay(true); // Request that GL view's SurfaceView be on top of other SurfaceViews (including CameraPreview's SurfaceView).

        cntRenderer.addView(preview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cntRenderer.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (glView != null) glView.onResume();

        renderThread = new MultiThreadRender.RenderThread(toolKit, glView);
        renderThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (glView != null) glView.onPause();

        cntRenderer.removeView(glView);
        cntRenderer.removeView(preview);
    }


    @Override
    public void onDraw(int markerId, float[] m) {

        float[] angles = toAxisAngle(m);

        int x = (int) ((m[12] / cameraWidth) * screenWidth);
        int y = -1 * (int) ((m[13] / cameraHeight) * screenHeight);


        float sx = (float) Math.sqrt(m[0] * m[0] + m[4] * m[4] + m[8] * m[8]);
        float sy = (float) Math.sqrt(m[1] * m[1] + m[5] * m[5] + m[9] * m[9]);
        float sz = (float) Math.sqrt(m[2] * m[2] + m[6] * m[6] + m[10] * m[10]);


        textView.setTranslationX(x);
        textView.setTranslationY(y);


        textView.setScaleX(sx);
        textView.setScaleY(sy);


        textView.setRotationX((float) (180 / Math.PI) * (angles[0] * angles[1] * -1));
        textView.setRotationY((float) (180 / Math.PI) * (angles[0] * angles[2]));
        textView.setRotation((float) (180 / Math.PI) * (angles[0] * angles[3] * -1));

        //Log.d("ROTATION", "x = " + textView.getRotationX());
        //Log.d("ROTATION", "y = "+textView.getRotationY());
        //Log.d("ROTATION", "z = "+textView.getRotation());

    }


    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    public float[] toAxisAngle(float[] m) {
        double angle, x, y, z;
        double epsilon = 0.01;
        double epsilon2 = 0.1;
        if ((Math.abs(m[4] - m[1]) < epsilon)
                && (Math.abs(m[8] - m[2]) < epsilon)
                && (Math.abs(m[6] - m[9]) < epsilon)) {
            if ((Math.abs(m[4] + m[1]) < epsilon2)
                    && (Math.abs(m[8] + m[2]) < epsilon2)
                    && (Math.abs(m[9] + m[6]) < epsilon2)
                    && (Math.abs(m[0] + m[5] + m[10] - 3) < epsilon2)) {
                return new float[]{0, 0, 1, 0,};
            }
            angle = Math.PI;
            double xx = (m[0] + 1) / 2;
            double yy = (m[5] + 1) / 2;
            double zz = (m[10] + 1) / 2;
            double xy = (m[4] + m[1]) / 4;
            double xz = (m[8] + m[2]) / 4;
            double yz = (m[9] + m[6]) / 4;
            if ((xx > yy) && (xx > zz)) {
                if (xx < epsilon) {
                    x = 0;
                    y = 0.7071;
                    z = 0.7071;
                } else {
                    x = Math.sqrt(xx);
                    y = xy / x;
                    z = xz / x;
                }
            } else if (yy > zz) {
                if (yy < epsilon) {
                    x = 0.7071;
                    y = 0;
                    z = 0.7071;
                } else {
                    y = Math.sqrt(yy);
                    x = xy / y;
                    z = yz / y;
                }
            } else {
                if (zz < epsilon) {
                    x = 0.7071;
                    y = 0.7071;
                    z = 0;
                } else {
                    z = Math.sqrt(zz);
                    x = xz / z;
                    y = yz / z;
                }
            }
            return new float[]{(float) angle, (float) x, (float) y, (float) z};
        }
        double s = Math.sqrt((m[6] - m[9]) * (m[6] - m[9])
                + (m[8] - m[2]) * (m[8] - m[2])
                + (m[1] - m[4]) * (m[1] - m[4]));
        if (Math.abs(s) < 0.001) s = 1;
        angle = Math.acos((m[0] + m[5] + m[10] - 1) / 2);
        x = (m[6] - m[9]) / s;
        y = (m[8] - m[2]) / s;
        z = (m[1] - m[4]) / s;
        return new float[]{(float) angle, (float) x, (float) y, (float) z};
    }
}