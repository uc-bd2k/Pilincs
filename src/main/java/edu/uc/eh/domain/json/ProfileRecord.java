package edu.uc.eh.domain.json;

import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.Profile;
import edu.uc.eh.datatypes.Tuples;

/**
 * Created by chojnasm on 8/3/15.
 */
public class ProfileRecord {
    private String assayType;
    private int runId;
    private String replicateId;
    private String cellId;
    private String pertIname;
    private String pertTime;
    private String pertDose;
    private String vector;
    private String correlatedVector;
    private String positiveCorrelation;
    private String positivePeptides;


    public ProfileRecord(Profile profile) {
        this.assayType = profile.getAssayType().toString();
        this.runId = profile.getRunId();
        this.replicateId = profile.getReplicateAnnotation().getReplicateId();
        this.cellId = profile.getReplicateAnnotation().getCellId();
        this.pertIname = profile.getReplicateAnnotation().getPertiname();
        this.pertTime = profile.getReplicateAnnotation().getPertTime();
        this.pertDose = profile.getReplicateAnnotation().getPertDose();

        this.vector = "<svg class=\"barchart\" vector="+ profile.getVectorJSON() +"></div>";
        this.correlatedVector = "<svg class=\"barchart\" vector="+ profile.getCorrelatedVectorJSON() +"></div>";
        this.positiveCorrelation = profile.getPositiveCorrelation();

        this.positivePeptides = profile.getPositivePeptides();

    }

    public int getRunId() {
        return runId;
    }

    public String getPositiveCorrelation() {
        return positiveCorrelation;
    }



    public String getAssayType() {
        return assayType;
    }

    public String getReplicateId() {
        return replicateId;
    }

    public void setReplicateId(String replicateId) {
        this.replicateId = replicateId;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getPertIname() {
        return pertIname;
    }

    public void setPertIname(String pertIname) {
        this.pertIname = pertIname;
    }

    public String getPertTime() {
        return pertTime;
    }

    public void setPertTime(String pertTime) {
        this.pertTime = pertTime;
    }

    public String getPertDose() {
        return pertDose;
    }

    public void setPertDose(String pertDose) {
        this.pertDose = pertDose;
    }

    public String getVector() {
        return vector;
    }

    public String getCorrelatedVector() {
        return correlatedVector;
    }

    public void setPositiveCorrelation(String positiveCorrelation) {
        this.positiveCorrelation = positiveCorrelation;
    }


    public String getPositivePeptides() {
        return positivePeptides;
    }

    public void setPositivePeptides(String positivePeptides) {
        this.positivePeptides = positivePeptides;
    }


}
