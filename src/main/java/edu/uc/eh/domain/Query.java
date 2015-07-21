package edu.uc.eh.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chojnasm on 7/20/15.
 */
public class Query {
    private final String text;

    @JsonCreator
    public Query(@JsonProperty("text") String content) {
        this.text = content;
    }

    public String getText() {
        return text;
    }
}
