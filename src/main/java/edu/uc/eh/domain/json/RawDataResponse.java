package edu.uc.eh.domain.json;

import java.util.List;

/**
 * Created by chojnasm on 7/23/15.
 */
public class RawDataResponse {
    private Integer total;
    private List<RawDataRecord> rows;

    public RawDataResponse(Long total, List<RawDataRecord> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<RawDataRecord> getRows() {
        return rows;
    }

    public void setRows(List<RawDataRecord> rows) {
        this.rows = rows;
    }
}
