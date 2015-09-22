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

package se.chalmers.agumentedexploration;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.rendering.ARRenderer;

import android.graphics.Color;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * A very simple example of extending ARActivity to create a new AR application.
 */
public class RenderActivity extends ARActivity implements SimpleRenderer.Callback {

	private SimpleRenderer myRenderer;
	private FrameLayout cntRenderer;
	private TextView textView;
	private View mDecorView;
	private int cameraWidth;
	private int cameraHeight;
	private int screenWidth;
	private int screenHeight;
	private FrameLayout.LayoutParams llp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.activity_main);

		mDecorView = getWindow().getDecorView();
		hideSystemUI();

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;



		cntRenderer = (FrameLayout)findViewById(R.id.cnt_render);

		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.cnt_root);

		textView = new TextView(this);
		textView.setText("Test");
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
		textView.setTextColor(Color.WHITE);


		//textView.setPivotY(0.5f);
		//textView.setPivotX(0.5f);
		textView.setGravity(Gravity.CENTER);

		llp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

		frameLayout.addView(textView, llp);


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode==21){
			myRenderer.stepMatrixIndex(-1);
		}else if(keyCode==22){
			myRenderer.stepMatrixIndex(1);
		}

		//myRenderer.stepMatrixIndex();

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Provide our own SimpleRenderer.
	 */
	@Override
	protected ARRenderer supplyRenderer() {

		myRenderer = new SimpleRenderer(this);;

		return myRenderer;
	}
	
	/**
	 * Use the FrameLayout in this Activity's UI.
	 */
	@Override
	protected FrameLayout supplyFrameLayout() {
		return cntRenderer;
	}

	@Override
	public void onDraw(int markerId, float[] m) {




		/*
		//T = {m[12], m[13], m[14]}




		m[0] = m[0] / sx;
		m[1] = m[1] / sy;
		m[2] = m[2] / sz;
		m[4] = m[4] / sx;
		m[5] = m[5] / sy;
		m[6] = m[6] / sz;
		m[8] = m[8] / sx;
		m[9] = m[9] / sy;
		m[10] = m[10] / sz;

		float ox = (float) Math.atan2(m[6],m[10]);
		float oy = (float) Math.atan2(-m[2],Math.sqrt(m[6]*m[6]+m[10]*m[10]));
		float oz = (float) Math.atan2(m[1],m[0]);






		double qw = Math.sqrt(1+matrix[0]+matrix[5]+matrix[10])/2;
		double qx = (matrix[9]-matrix[7])/(4*qw);
		double qy = (matrix[2]-matrix[8])/(4*qw);
		double qz = (matrix[4]-matrix[1])/(4*qw);

		double s = Math.sqrt(1-(qw*qw));
		double x = qx / s;
		double y = qy / s;
		double z = qz / s;

		 // Math.sqrt(1+matrix[0]+matrix[5]+matrix[10])/2;

		//Matrix.setIdentityM(matrix, 0);

		double angle = 90;//2 * Math.acos(qw);

		*/

		float[] angles = toAxisAngle(m);

		int x = (int)(( m[12] / cameraWidth) * screenWidth);
		int y = -1*(int)((m[13] /cameraHeight) * screenHeight);


		float sx = (float) Math.sqrt(m[0]*m[0] + m[4] * m[4] + m[8] * m[8]);
		float sy = (float) Math.sqrt(m[1]*m[1] + m[5] * m[5] + m[9] * m[9]);
		float sz = (float) Math.sqrt(m[2]*m[2] + m[6] * m[6] + m[10] * m[10]);



		textView.setTranslationX(x);
		textView.setTranslationY(y);

		Log.d("ROTATION", "scaleX = " + sx);
		Log.d("ROTATION", "scaleY = " + sy);

		textView.setScaleX(sx);
		textView.setScaleY(sy);


		/*llp.leftMargin = ;
		llp.topMargin =  ;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textView.setLayoutParams(llp);
			}
		});*/


		textView.setRotationX((float) (180 / Math.PI) * (angles[0] * angles[1] * -1));
		textView.setRotationY((float) (180 / Math.PI) * (angles[0] * angles[2]));
		textView.setRotation((float) (180 / Math.PI) * (angles[0] * angles[3] * -1));

		//Log.d("ROTATION", "x = " + textView.getRotationX());
		//Log.d("ROTATION", "y = "+textView.getRotationY());
		//Log.d("ROTATION", "z = "+textView.getRotation());

	}

	/*public float[] rotate(float[]  m) {
		// Assuming the angles are in radians.
		if (m.m10 > 0.998) { // singularity at north pole
			heading = Math.atan2(m.m02,m.m22);
			attitude = Math.PI/2;
			bank = 0;
			return;
		}
		if (m.m10 < -0.998) { // singularity at south pole
			heading = Math.atan2(m.m02,m.m22);
			attitude = -Math.PI/2;
			bank = 0;
			return;
		}
		heading = Math.atan2(-m.m20,m.m00);
		bank = Math.atan2(-m.m12,m.m11);
		attitude = Math.asin(m.m10);
	}*/

	@Override
	public void cameraPreviewStarted(int width, int height, int rate, int cameraIndex, boolean cameraIsFrontFacing) {
		super.cameraPreviewStarted(width, height, rate, cameraIndex, cameraIsFrontFacing);
		cameraWidth = width;
		cameraHeight = height;
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
		double angle,x,y,z; // variables for result
		double epsilon = 0.01; // margin to allow for rounding errors
		double epsilon2 = 0.1; // margin to distinguish between 0 and 180 degrees
		// optional check that input is pure rotation, 'isRotationMatrix' is defined at:
		// http://www.euclideanspace.com/maths/algebra/matrix/orthogonal/rotation/
		if ((Math.abs(m[4]-m[1])< epsilon)
				&& (Math.abs(m[8]-m[2])< epsilon)
				&& (Math.abs(m[6]-m[9])< epsilon)) {
			// singularity found
			// first check for identity matrix which must have +1 for all terms
			//  in leading diagonaland zero in other terms
			if ((Math.abs(m[4]+m[1]) < epsilon2)
					&& (Math.abs(m[8]+m[2]) < epsilon2)
					&& (Math.abs(m[9]+m[6]) < epsilon2)
					&& (Math.abs(m[0]+m[5]+m[10]-3) < epsilon2)) {
				// this singularity is identity matrix so angle = 0
				log("test1",0,0,1,0);
				return new float[]{0,0,1,0,}; // zero angle, arbitrary axis
			}
			// otherwise this singularity is angle = 180
			angle = Math.PI;
			double xx = (m[0]+1)/2;
			double yy = (m[5]+1)/2;
			double zz = (m[10]+1)/2;
			double xy = (m[4]+m[1])/4;
			double xz = (m[8]+m[2])/4;
			double yz = (m[9]+m[6])/4;
			if ((xx > yy) && (xx > zz)) { // m[0][0] is the largest diagonal term
				if (xx< epsilon) {
					x = 0;
					y = 0.7071;
					z = 0.7071;
				} else {
					x = Math.sqrt(xx);
					y = xy/x;
					z = xz/x;
				}
			} else if (yy > zz) { // m[1][1] is the largest diagonal term
				if (yy< epsilon) {
					x = 0.7071;
					y = 0;
					z = 0.7071;
				} else {
					y = Math.sqrt(yy);
					x = xy/y;
					z = yz/y;
				}
			} else { // m[2][2] is the largest diagonal term so base result on this
				if (zz< epsilon) {
					x = 0.7071;
					y = 0.7071;
					z = 0;
				} else {
					z = Math.sqrt(zz);
					x = xz/z;
					y = yz/z;
				}
			}
			log("test2",angle,x,y,z);
			return new float[]{(float)angle,(float)x,(float)y,(float)z}; // return 180 deg rotation
		}
		// as we have reached here there are no singularities so we can handle normally
		double s = Math.sqrt((m[6] - m[9])*(m[6] - m[9])
				+(m[8] - m[2])*(m[8] - m[2])
				+(m[1] - m[4])*(m[1] - m[4])); // used to normalise
		if (Math.abs(s) < 0.001) s=1;
		// prevent divide by zero, should not happen if matrix is orthogonal and should be
		// caught by singularity test above, but I've left it in just in case
		angle = Math.acos(( m[0] + m[5] + m[10] - 1)/2);
		x = (m[6] - m[9])/s;
		y = (m[8] - m[2])/s;
		z = (m[1] - m[4])/s;
		log("ttest 3 ",angle,x,y,z);
		return new float[]{(float)angle,(float)x,(float)y,(float)z};
	}

	private void log(String id, double angle, double x, double y, double z) {
		//Log.d(TAG,id+" angle = "+angle+", x = "+x+", y = "+y+", z = "+z);
	}
}