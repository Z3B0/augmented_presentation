package se.chalmers.ocuclass.net;

import com.google.gson.annotations.SerializedName;

/**
 * Created by richard on 14/10/15.
 */
public class BaseResponse {



    @SerializedName("error")
    public String error;

    @SerializedName("message")
    public String message;

    public boolean success;
}
