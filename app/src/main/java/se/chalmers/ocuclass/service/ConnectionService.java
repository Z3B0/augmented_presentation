package se.chalmers.ocuclass.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by richard on 13/10/15.
 */
public class ConnectionService extends Service {
    public static final String EVENT_WEAR_DIRECTION = "wear-event";
    public static final String EXTRA_DIRECTION = "extra_direction";


    public static final int MSG_DIRECTION = 1;

    @Override
    public void onCreate() {
        super.onCreate();

    }


    private void sendWearEvent(String direction) {
        Intent intent = new Intent(EVENT_WEAR_DIRECTION);
        // add data
        intent.putExtra(EXTRA_DIRECTION, direction);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
