package se.chalmers.agumentedexploration.model;

import java.io.Serializable;

/**
 * Created by richard on 24/09/15.
 */
public class User implements Serializable{

    private String username;
    private String name;
    private String userId;

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
