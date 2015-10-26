package se.chalmers.ocuclass.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by richard on 14/10/15.
 */
public class PresentationEvent implements Serializable {

    @SerializedName("event")
    private String event;

    public PresentationEvent(String direction) {
        this.event = direction;
    }


    public String getEvent() {
        return event;
    }
}
