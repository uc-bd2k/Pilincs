package edu.uc.eh.domain.json;

import edu.uc.eh.domain.json.AssayRecord;

import java.util.List;

/**
 * Created by chojnasm on 7/23/15.
 */
public class TableResponse {
    private Integer total;
    private List<AssayRecord> rows;

    public TableResponse(Long total, List<AssayRecord> rows) {
        this.total = total.intValue();
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<AssayRecord> getRows() {
        return rows;
    }

    public void setRows(List<AssayRecord> rows) {
        this.rows = rows;
    }
}
