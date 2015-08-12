package edu.uc.eh.domain.json;

import java.util.List;

/**
 * Created by chojnasm on 8/12/15.
 */
public class HeatMapResponse {

    private List<String> peptideNames;
    private List<String> profileNames;
    private List<MatrixCell> cells;

    public HeatMapResponse(List<String> peptideNames, List<String> profileNames, List<MatrixCell> cells) {
        this.peptideNames = peptideNames;
        this.profileNames = profileNames;
        this.cells = cells;
    }

    public List<String> getPeptideNames() {
        return peptideNames;
    }

    public void setPeptideNames(List<String> peptideNames) {
        this.peptideNames = peptideNames;
    }

    public List<String> getProfileNames() {
        return profileNames;
    }

    public void setProfileNames(List<String> profileNames) {
        this.profileNames = profileNames;
    }

    public List<MatrixCell> getCells() {
        return cells;
    }

    public void setCells(List<MatrixCell> cells) {
        this.cells = cells;
    }

}
