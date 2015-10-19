

package se.chalmers.ocuclass;

import android.app.Application;

import se.chalmers.ocuclass.net.RestClient;

public class OcuclassApplication extends Application {


    
    @Override
    public void onCreate() {
    	super.onCreate();

        RestClient.getInstance().init(getApplicationContext());
    }
}
