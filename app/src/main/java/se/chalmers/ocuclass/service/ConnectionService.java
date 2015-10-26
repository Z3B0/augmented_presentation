package se.chalmers.ocuclass.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by richard on 13/10/15.
 */
public class ConnectionService extends Service {

    private static final String TAG = ConnectionService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "===================================");
        Log.d(TAG, "===================================");
        Log.d(TAG,"===================================");
        Log.d(TAG,"===================================");
        Log.d(TAG,"==== THE SERVICE IS CREATED!!!!====");
        Log.d(TAG,"===================================");
        Log.d(TAG,"===================================");
        Log.d(TAG,"===================================");
        Log.d(TAG,"===================================");
        Log.d(TAG,"===================================");

    }

    public class ServiceBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


}
