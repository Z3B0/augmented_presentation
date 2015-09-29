package se.chalmers.agumentedexploration.net;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by richard on 24/09/15.
 */
public interface RestService {


    @GET("login")
    Observable<LoginResponse> login(@Query("username") String username, @Query("password")String password);


}
