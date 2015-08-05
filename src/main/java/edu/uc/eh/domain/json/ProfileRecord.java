package edu.uc.eh.domain.json;

import edu.uc.eh.domain.Profile;
import edu.uc.eh.utils.Tuples;

import java.util.List;

/**
 * Created by chojnasm on 8/3/15.
 */
public class ProfileRecord {
    private String assayType;
    private String replicateId;
    private String cellId;
    private String pertIname;
    private String pertTime;
    private String pertDose;
    private String vector;
    private String positiveCorrelation;
    private String negativeCorrelation;



    public ProfileRecord(Profile profile) {
        this.assayType = profile.getAssayType().toString();
        this.replicateId = profile.getReplicateAnnotation().getReplicateId();
        this.cellId = profile.getReplicateAnnotation().getCellId();
        this.pertIname = profile.getReplicateAnnotation().getPertiname();
        this.pertTime = profile.getReplicateAnnotation().getPertTime();
        this.pertDose = profile.getReplicateAnnotation().getPertDose();

        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for(Tuples.Tuple2<String,Double> tuple2 : profile.getVector()){

            Double multi;
            if(tuple2 == null || tuple2.getT2() == null){
                multi = 0.0;
            }else{
                multi = tuple2.getT2()*10.0;
            }

            sb.append(multi.intValue());
            if(profile.getVector().size()-1 == counter++)
                break;
            sb.append(",");
        }
        this.vector = "<span class=\"barchart\">"+sb.toString().replace("[","").replace("]","")+"</span>";
        this.positiveCorrelation = profile.getPositiveCorrelation();
        this.negativeCorrelation = profile.getNegativeCorrelation();
    }

    public String getPositiveCorrelation() {
        return positiveCorrelation;
    }

    public String getNegativeCorrelation() {
        return negativeCorrelation;
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

}
