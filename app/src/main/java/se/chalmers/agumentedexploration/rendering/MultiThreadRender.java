package se.chalmers.agumentedexploration.rendering;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.artoolkit.ar.base.ARToolKit;

/**
 * Created by richard on 24/09/15.
 */
public class MultiThreadRender {

    private static final String EXTRA_FRAME_BYTES = "array";

    public static class FrameHandler extends Handler{

        private static final String EXTRA_FRAME_BYTES = "array";

        private ARToolKit toolKit;
        private GLSurfaceView glSurfaceView;


        public FrameHandler(ARToolKit toolKit, GLSurfaceView glSurfaceView) {
            this.toolKit = toolKit;
            this.glSurfaceView = glSurfaceView;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();

            if (toolKit.convertAndDetect(data.getByteArray(EXTRA_FRAME_BYTES))) {

                // Update the renderer as the frame has changed
                if (glSurfaceView != null) glSurfaceView.requestRender();
            }
        }
    }

    public static class RenderThread extends Thread {

        private Handler handler;
        private ARToolKit toolKit;
        private GLSurfaceView glSurfaceView;

        public RenderThread(ARToolKit toolKit, GLSurfaceView glSurfaceView) {
            this.toolKit = toolKit;
            this.glSurfaceView = glSurfaceView;
        }

        @Override
        public void run(){
            Looper.prepare();

            handler = new FrameHandler(toolKit,glSurfaceView);
            Looper.loop();
        }

        public void handleFrame(byte[] frame) {
            Message msgObj = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putByteArray(EXTRA_FRAME_BYTES,frame);
            msgObj.setData(b);
            handler.sendMessage(msgObj);
        }
    }
}
