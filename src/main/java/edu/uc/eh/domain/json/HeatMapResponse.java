package edu.uc.eh.domain.json;

import edu.uc.eh.datatypes.PeptideOrder;

import java.util.List;

/**
 * Created by chojnasm on 8/12/15.
 */
public class HeatMapResponse {

    private List<PeptideOrder> peptideNames;
    private List<String> profileNames;
    private List<MatrixRow> rows;

    public HeatMapResponse(List<PeptideOrder> peptideNames, List<String> profileNames, List<MatrixRow> rows) {
        this.peptideNames = peptideNames;
        this.profileNames = profileNames;
        this.rows = rows;
    }

    public List<PeptideOrder> getPeptideNames() {
        return peptideNames;
    }

    public void setPeptideNames(List<PeptideOrder> peptideNames) {
        this.peptideNames = peptideNames;
    }

    public List<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(List<String> profileNames) {
        this.profileNames = profileNames;
    }

    public List<MatrixRow> getRows() {
        return rows;
    }

    public void setRows(List<MatrixRow> rows) {
        this.rows = rows;
    }
}
