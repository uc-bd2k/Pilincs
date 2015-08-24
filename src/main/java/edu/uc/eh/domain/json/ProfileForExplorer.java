package edu.uc.eh.domain.json;

import java.util.List;

/**
 * Created by chojnasm on 8/17/15.
 */
public class ProfileForExplorer {
    private List<String> cells;
    private List<String> perturbations;
    private List<String> doses;
    private List<String> times;
    private List<int[]> profiles;

    public ProfileForExplorer(List<String> cells, List<String> perturbations, List<String> doses, List<String> times, List<int[]> profiles) {
        this.cells = cells;
        this.perturbations = perturbations;
        this.doses = doses;
        this.times = times;
        this.profiles = profiles;

    }

    public List<String> getCells() {
        return cells;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    public List<String> getPerturbations() {
        return perturbations;
    }

    public void setPerturbations(List<String> perturbations) {
        this.perturbations = perturbations;
    }

    public List<String> getDoses() {
        return doses;
    }

    public void setDoses(List<String> doses) {
        this.doses = doses;
    }

    public List<String> getTimes() {
        return times;
    }

    public void setTimes(List<String> times) {
        this.times = times;
    }

    public List<int[]> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<int[]> profiles) {
        this.profiles = profiles;
    }
}
