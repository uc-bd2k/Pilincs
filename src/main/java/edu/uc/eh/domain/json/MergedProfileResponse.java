package edu.uc.eh.domain.json;

import java.util.List;

/**
 * Created by chojnasm on 9/15/15.
 */
public class MergedProfileResponse {
    private Integer total;
    private List<MergedProfileRecord> rows;

    public MergedProfileResponse(Long total, List<MergedProfileRecord> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<MergedProfileRecord> getRows() {
        return rows;
    }

    public void setRows(List<MergedProfileRecord> rows) {
        this.rows = rows;
    }
}
