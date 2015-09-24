/*
 *  ARActivity.java
 *  ARToolKit5
 *
 *  This file is part of ARToolKit.
 *
 *  ARToolKit is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ARToolKit is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with ARToolKit.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  As a special exception, the copyright holders of this library give you
 *  permission to link this library with independent modules to produce an
 *  executable, regardless of the license terms of these independent modules, and to
 *  copy and distribute the resulting executable under terms of your choice,
 *  provided that you also meet, for each linked independent module, the terms and
 *  conditions of the license of that module. An independent module is a module
 *  which is neither derived from nor based on this library. If you modify this
 *  library, you may extend this exception to your version of the library, but you
 *  are not obligated to do so. If you do not wish to do so, delete this exception
 *  statement from your version.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package org.artoolkit.ar.base;

import org.artoolkit.ar.base.NativeInterface;

import org.artoolkit.ar.base.R;
import org.artoolkit.ar.base.camera.CameraEventListener;
import org.artoolkit.ar.base.camera.CameraPreferencesActivity;
import org.artoolkit.ar.base.camera.CaptureCameraPreview;
import org.artoolkit.ar.base.rendering.ARRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
//import android.os.AsyncTask;
//import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Observable;


public abstract class ARActivity extends Activity implements CameraEventListener {

	/**
	 * Android logging tag for this class.
	 */
	protected final static String TAG = "ARActivity";
	
	/**
	 * Camera preview which will provide video frames.
	 */
	private CaptureCameraPreview preview;
	
	/**
	 * GL surface to render the virtual objects	 
	 */
	private GLSurfaceView glView;	
	

	protected ARRenderer renderer;
	private ARToolKit toolKit = new ARToolKit();

	protected FrameLayout mainLayout; 

	private boolean firstUpdate = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        // This needs to be done just only the very first time the application is run,
        // or whenever a new preference is added (e.g. after an application upgrade).
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);



        AndroidUtils.reportDisplayInformation(this);
    }
	
    /**
     * Allows subclasses to supply a custom {@link Renderer}.
     * @return The {@link Renderer} to use.
	 * @param toolKit
     */
    protected abstract ARRenderer supplyRenderer(ARToolKit toolKit);
    
    /**
     * Allows subclasses to supply a {@link FrameLayout} which will be populated
     * with a camera preview and GL surface view.
     * @return The {@link FrameLayout} to use.
     */
    protected abstract FrameLayout supplyFrameLayout();
        
	@Override
    protected void onStart() {

    	super.onStart();    

    	Log.i(TAG, "Activity starting.");
    	

    			       
    }
    
    @SuppressWarnings("deprecation") // FILL_PARENT still required for API level 7 (Android 2.1)
	@Override
    public void onResume() {
    	//Log.i(TAG, "onResume()");
    	super.onResume();
    	
    	// Create the camera preview
    	preview = new CaptureCameraPreview(this, this);
    	
    	Log.i(TAG, "CaptureCameraPreview created"); 
    	
    	// Create the GL view
    	glView = new GLSurfaceView(this);    		
		glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glView.getHolder().setFormat(PixelFormat.TRANSLUCENT); // Needs to be a translucent surface so the camera preview shows through.
		glView.setRenderer(renderer);		
		glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // Only render when we have a frame (must call requestRender()).
		glView.setZOrderMediaOverlay(true); // Request that GL view's SurfaceView be on top of other SurfaceViews (including CameraPreview's SurfaceView).
		
		Log.i(TAG, "GLSurfaceView created");
		
		// Add the views to the interface
        mainLayout.addView(preview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mainLayout.addView(glView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		Log.i(TAG, "Views added to main layout.");

		if (glView != null) glView.onResume();
    }
    
	@Override
	protected void onPause() {
    	//Log.i(TAG, "onPause()");
	    super.onPause();
	    
	    if (glView != null) glView.onPause();
	    
	    // System hardware must be released in onPause(), so it's available to
	    // any incoming activity. Removing the CameraPreview will do this for the
	    // camera. Also do it for the GLSurfaceView, since it serves no purpose
	    // with the camera preview gone.
	    mainLayout.removeView(glView);
	    mainLayout.removeView(preview);
	}
	
	@Override 
	public void onStop() {
    	Log.i(TAG, "Activity stopping.");
		
		super.onStop();
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.settings) {
			startActivity(new Intent(this, CameraPreferencesActivity.class));
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	

    
    //
    // At present, the underlying ARWrapper is not thread-safe,
    // so this multi-threaded version is set aside in favour of a single-threaded version
    //
    /*
    private class ConvertAndDetectTask extends AsyncTask<byte[], Void, Boolean> {

    	@Override
		protected Boolean doInBackground(byte[]... frame) {			
			return (ARToolKit.getInstance().convertAndDetect(frame[0]));			
		}
    	
    	@Override
    	protected void onPostExecute(Boolean result) {
    		
			if (firstUpdate) {
				
				firstUpdate = false;
				
				// ARToolKit has been initialised. The renderer can now add markers, etc...
				if (renderer.configureARScene()) {
					Log.i(TAG, "Scene configured successfully");
				} else { 
					// Error
					Log.e(TAG, "Error configuring scene. Cannot continue.");
					finish();
				}
			}
			
			
			// Update the renderer as the frame has changed
			if (glView != null) glView.requestRender();
			
			onFrameProcessed();
			
		}
		
    }
    
    ConvertAndDetectTask task;
    
	@Override
	public void cameraPreviewFrame(byte[] frame) {
	
		if (task == null || task.getStatus() == Status.FINISHED) {		
			task = new ConvertAndDetectTask();
			task.execute(frame);
		}
	}
    
    boolean cleanupFlag = false;
    
    public void onFrameProcessed() {
    	if (cleanupFlag) {
    		ARToolKit.getInstance().cleanup();
    		cleanupFlag = false;
    	}
    }
    
	@Override
	public void cameraPreviewStopped() {
		if (task != null && task.getStatus() != Status.FINISHED) {
			cleanupFlag = true;	
		} else {
			ARToolKit.getInstance().cleanup();
		}
	}	
	*/
    

    
	@Override
	public void cameraPreviewStopped() {
		toolKit.cleanup();
	}	

	protected void showInfo() {
    	
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		
		dialogBuilder.setMessage("ARToolKit Version: " + NativeInterface.arwGetARToolKitVersion());
		
		dialogBuilder.setCancelable(false);
		dialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		
		AlertDialog alert = dialogBuilder.create();
		alert.setTitle("ARToolKit");
		alert.show();
    	
		
    }

}
