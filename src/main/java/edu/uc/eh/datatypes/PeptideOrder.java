package edu.uc.eh.datatypes;

import java.io.Serializable;

/**
 * Created by chojnasm on 8/15/15.
 */
public class PeptideOrder implements Serializable {
//    private int naturalOrder;

    private String peptideName;
    private int clusterOrder;

    public PeptideOrder(String peptideName, int clusterOrder) {
//        this.naturalOrder = naturalOrder;
        this.clusterOrder = clusterOrder;
        this.peptideName = peptideName;
    }

//    public int getNaturalOrder() {
//        return naturalOrder;
//    }
//
//    public void setNaturalOrder(int naturalOrder) {
//        this.naturalOrder = naturalOrder;
//    }

    public int getClusterOrder() {
        return clusterOrder;
    }

    public void setClusterOrder(int clusterOrder) {
        this.clusterOrder = clusterOrder;
    }

    public String getPeptideName() {
        return peptideName;
    }

    public void setPeptideName(String peptideName) {
        this.peptideName = peptideName;
    }
}
