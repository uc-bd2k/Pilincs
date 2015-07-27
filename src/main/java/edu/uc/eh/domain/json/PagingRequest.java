package edu.uc.eh.domain.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by chojnasm on 7/24/15.
 */
public class PagingRequest {
    private String order;
    private Integer limit;
    private Integer offset;

    @JsonCreator
    public PagingRequest(@JsonProperty("order")String order,
                         @JsonProperty("limit") Integer limit,
                         @JsonProperty("offset") Integer offset) {
        this.order = order;
        this.limit = limit;
        this.offset = offset;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
