package se.chalmers.agumentedexploration.net;

import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by richard on 24/09/15.
 */
public class RestClient {

    private final String ENDPOINT_URL = "http://cgilabs.se/arexp/";
    private RestService service;

    private static class Holder {
        static final RestClient INSTANCE = new RestClient();
    }

    private RestClient() {




        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        service = retrofit.create(RestService.class);



    }


    public static RestClient getInstance() {
        return Holder.INSTANCE;
    }

    public static RestService service() {
        return Holder.INSTANCE.service;
    }
}
