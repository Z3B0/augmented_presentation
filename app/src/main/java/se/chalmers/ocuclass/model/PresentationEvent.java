package se.chalmers.ocuclass.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by richard on 14/10/15.
 */
public class PresentationEvent {

    @SerializedName("event")
    private String event;

    public PresentationEvent(String direction) {
        event = direction;
    }


    public String getEvent() {
        return event;
    }
}
