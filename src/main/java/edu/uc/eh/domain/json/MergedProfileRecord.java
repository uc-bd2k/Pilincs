package edu.uc.eh.domain.json;

/**
 * Created by chojnasm on 9/15/15.
 */
public class MergedProfileRecord {

    private String nTuple;
    private String vector;

    public MergedProfileRecord(String nTuple, String chart) {
        this.nTuple = nTuple;
        this.vector = chart;
    }

    public String getnTuple() {
        return nTuple;
    }

    public void setnTuple(String nTuple) {
        this.nTuple = nTuple;
    }


    public String getVector() {
        return vector;
    }

    public void setVector(String vector) {
        this.vector = vector;
    }
}
