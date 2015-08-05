package edu.uc.eh.domain.json;

import java.util.List;

/**
 * Created by chojnasm on 8/3/15.
 */
public class ProfileResponse {
    private Integer total;
    private List<ProfileRecord> rows;

    public ProfileResponse(Long total, List<ProfileRecord> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ProfileRecord> getRows() {
        return rows;
    }

    public void setRows(List<ProfileRecord> rows) {
        this.rows = rows;
    }
}
