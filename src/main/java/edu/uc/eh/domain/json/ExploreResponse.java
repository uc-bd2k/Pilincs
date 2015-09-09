package edu.uc.eh.domain.json;

import edu.uc.eh.datatypes.Int4Tuple;

import java.util.List;

/**
 * Created by chojnasm on 9/3/15.
 */
public class ExploreResponse {
    private List<String> cellNames;
    private List<String> pertNames;
    private List<String> doseNames;
    private List<String> timeNames;
    private List<Int4Tuple> rows; //cellNameId, pertNameId, doseNameId, timeNameId

    public ExploreResponse(List<String> cellNames, List<String> pertNames, List<String> doseNames, List<String> timeNames, List<Int4Tuple> rows) {
        this.cellNames = cellNames;
        this.pertNames = pertNames;
        this.doseNames = doseNames;
        this.timeNames = timeNames;
        this.rows = rows;
    }

    public List<String> getCellNames() {
        return cellNames;
    }

    public void setCellNames(List<String> cellNames) {
        this.cellNames = cellNames;
    }

    public List<String> getPertNames() {
        return pertNames;
    }

    public void setPertNames(List<String> pertNames) {
        this.pertNames = pertNames;
    }

    public List<String> getDoseNames() {
        return doseNames;
    }

    public void setDoseNames(List<String> doseNames) {
        this.doseNames = doseNames;
    }

    public List<String> getTimeNames() {
        return timeNames;
    }

    public void setTimeNames(List<String> timeNames) {
        this.timeNames = timeNames;
    }

    public List<Int4Tuple> getRows() {
        return rows;
    }

    public void setRows(List<Int4Tuple> rows) {
        this.rows = rows;
    }
}

