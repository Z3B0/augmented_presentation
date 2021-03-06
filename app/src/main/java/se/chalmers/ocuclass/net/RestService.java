package se.chalmers.ocuclass.net;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.PresentationEvent;
import se.chalmers.ocuclass.model.User;

/**
 * Created by richard on 24/09/15.
 */
public interface RestService {


    /*@GET("login")
    Observable<User> login(@Query("username") String username, @Query("password")String password);*/

    @POST("login")
    Observable<User> login(@Body User user);

    @POST("register")
    Observable<User> register(@Body User user);

    @GET("presentation/current")
    Observable<Presentation> presentation();

    @GET("presentation")
    Observable<List<Presentation>> presentationList();

    @GET("presentation/viewers/{id}")
    Observable<List<User>> presentationViewers(@Path("id") String presentationId);


    @GET("presentation/start/{id}")
    Observable<BaseResponse> presentationStart(@Path("id") String presentationId);

    @GET("presentation/join/{id}/{userId}/{deviceId}")
    Observable<BaseResponse> presentationJoin(@Path("id") String presentationId, @Path("userId") String userId, @Path("deviceId") String deviceId);

    @GET("presentation/leave/{id}/{userId}")
    Observable<BaseResponse> presentationLeave(@Path("id") String presentationId, @Path("userId") String userId);

    @GET("presentation/rate/{id}/{slide}/{userId}")
    Observable<BaseResponse> presentations(@Path("id") String presentationId, @Path("slide") String slideId, @Path("userId") String username);

    @Headers("Content-Type: application/json")
    @POST("event/{id}")
    Observable<BaseResponse> postEvent(@Path("id") String presentationId,@Body PresentationEvent event);

    @GET("event/{id}")
    Observable<PresentationEvent> getEvent(@Path("id") String presentationId);





}
