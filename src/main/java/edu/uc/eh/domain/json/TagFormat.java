package edu.uc.eh.domain.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chojnasm on 7/21/15.
 */
public class TagFormat {
    private final String name;
    private final String flag;
    private final String annotation;
    private final Integer rank;

    @JsonCreator
    public TagFormat(@JsonProperty("name") String name,
                     @JsonProperty("flag") String flag,
                     @JsonProperty("annotation") String annotation,
                     @JsonProperty("rank") Integer rank) {
        this.name = name;
        this.flag = flag;
        this.annotation = annotation;
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagFormat)) return false;

        TagFormat tagFormat = (TagFormat) o;

        return !(getName() != null ? !getName().equals(tagFormat.getName()) : tagFormat.getName() != null);

    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public String getFlag() {
        return flag;
    }

    public String getAnnotation() {
        return annotation;
    }

    public Integer getRank() {
        return rank;
    }
}
