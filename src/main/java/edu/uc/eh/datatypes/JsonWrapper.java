package edu.uc.eh.datatypes;

import java.io.Serializable;

/**
 * Created by chojnasm on 9/17/15.
 */
public class JsonWrapper implements Serializable {

    private final String json;

    public JsonWrapper(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }
}
