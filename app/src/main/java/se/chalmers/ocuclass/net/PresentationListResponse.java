package se.chalmers.ocuclass.net;

import java.util.List;

import se.chalmers.ocuclass.model.Presentation;

/**
 * Created by richard on 01/10/15.
 */
public class PresentationListResponse {
    private List<Presentation> presentations;

    public void setPresentations(List<Presentation> presentations) {
        this.presentations = presentations;
    }

    public List<Presentation> getPresentations() {
        return presentations;
    }
}
