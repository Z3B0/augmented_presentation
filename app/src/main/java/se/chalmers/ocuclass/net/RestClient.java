package se.chalmers.ocuclass.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import se.chalmers.ocuclass.model.User;

/**
 * Created by richard on 24/09/15.
 */
public class RestClient {



    private static final String ENDPOINT_URL = "https://ocuclass.herokuapp.com/";
    private static final String PREF_KEY_USER = "pref_key_user";


    private RestService service;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private User user;

    public void init(Context appContext) {
        prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        this.user = gson.fromJson(prefs.getString(PREF_KEY_USER,null),User.class);
    }

    private static class Holder {
        static final RestClient INSTANCE = new RestClient();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user, String password){
        if(user!=null) {
            user.setPassword(password);
        }
        this.user = user;
        prefs.edit().putString(PREF_KEY_USER,gson.toJson(user)).apply();
    }


    private RestClient() {




        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if(user != null){
                    return chain.proceed(chain.request().newBuilder().addHeader("Authorization", "Basic "+ Base64.encodeToString((user.getUsername()+":"+user.getPassword()).getBytes(), Base64.NO_WRAP)).build());
                }

                return chain.proceed(chain.request());
            }
        });


        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT_URL)
                .client(httpClient)
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
