package se.chalmers.ocuclass.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by richard on 01/10/15.
 */
public class Slide implements Serializable {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("background")
    @Expose
    private String background;
    @SerializedName("3dmodel")
    @Expose
    private Object _3dmodel;

    /**
     *
     * @return
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     * The background
     */
    public String getBackground() {
        return background;
    }

    /**
     *
     * @param background
     * The background
     */
    public void setBackground(String background) {
        this.background = background;
    }

    /**
     *
     * @return
     * The _3dmodel
     */
    public Object get3dmodel() {
        return _3dmodel;
    }

    /**
     *
     * @param _3dmodel
     * The 3dmodel
     */
    public void set3dmodel(Object _3dmodel) {
        this._3dmodel = _3dmodel;
    }

}
