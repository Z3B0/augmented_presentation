package se.chalmers.ocuclass.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.ocuclass.net.BaseResponse;

/**
 * Created by richard on 01/10/15.
 */
public class Presentation extends BaseResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("speakers")
    @Expose
    private List<User> speakers = new ArrayList<User>();
    @SerializedName("slides")
    @Expose
    private List<Slide> slides = new ArrayList<Slide>();

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The speakers
     */
    public List<User> getSpeakers() {
        return speakers;
    }

    /**
     *
     * @param speakers
     * The speakers
     */
    public void setSpeakers(List<User> speakers) {
        this.speakers = speakers;
    }

    /**
     *
     * @return
     * The slides
     */
    public List<Slide> getSlides() {
        return slides;
    }

    /**
     *
     * @param slides
     * The slides
     */
    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
