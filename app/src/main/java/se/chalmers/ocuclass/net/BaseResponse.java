package se.chalmers.ocuclass.net;

import java.io.Serializable;

/**
 * Created by richard on 20/10/15.
 */
public class BaseResponse implements Serializable {


    private boolean success = false;

    public boolean isSuccessful() {
        return success;
    }
}
