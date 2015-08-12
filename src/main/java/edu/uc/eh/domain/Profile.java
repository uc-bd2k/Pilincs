package edu.uc.eh.domain;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.ListWrapper;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.datatypes.Tuples;
import edu.uc.eh.utils.UtilsFormat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chojnasm on 7/31/15.
 */

@Entity
public class Profile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ReplicateAnnotation replicateAnnotation;

    @ManyToOne
    private GctFile gctFile;

    private AssayType assayType;

    /**
     * Values for peptides, size of vector = number of peptides in full profile
     */
    @Lob
    private ListWrapper vector; // should be linked-hash map

    private String positiveCorrelation;
    private String negativeCorrelation;

    @Lob
    private String positivePeptides;

    @Lob
    private String negativePeptides;

    public Profile(ReplicateAnnotation replicateAnnotation, GctFile gctFile,
                   double[] vector,
                   boolean[] imputes,
                   List<String> referenceProfile) {

        this.replicateAnnotation = replicateAnnotation;
        this.gctFile = gctFile;
        this.assayType = gctFile.getAssayType();
        this.vector = new ListWrapper(vector,imputes, referenceProfile);


    }

    public Profile() {
    }

    @Override
    public String toString() {
        return "<b>Assay: </b> " + getAssayType() +
                "<br/><b>RunId: </b> " + getRunId() +
                "<br/><b>ReplicateId: </b>" + replicateAnnotation.getReplicateId() +
                "<br/><b>PertIname: </b>" + replicateAnnotation.getPertiname() +
                "<br/><b>CellId: </b>" + replicateAnnotation.getCellId() +
                "<br/><b>Dose: </b>" + replicateAnnotation.getPertDose() +
                "<br/><b>Time: </b>" + replicateAnnotation.getPertTime() +
                "";
    }

    public String getPositiveCorrelation() {
        return positiveCorrelation;
    }

    public void setPositiveCorrelation(String positiveCorrelation) {
        this.positiveCorrelation = positiveCorrelation;
    }

    public String getNegativeCorrelation() {
        return negativeCorrelation;
    }

    public void setNegativeCorrelation(String negativeCorrelation) {
        this.negativeCorrelation = negativeCorrelation;
    }

    public Long getId() {
        return id;
    }

    public ReplicateAnnotation getReplicateAnnotation() {
        return replicateAnnotation;
    }

    public AssayType getAssayType() {
        return gctFile.getAssayType();
    }

    public int getRunId() {
        return gctFile.getRunId();
    }

    public double[] getVector() {
        return vector.getDoubles();
    }


    public void setPositivePeptides(String positivePeptides) {
        this.positivePeptides = positivePeptides;
    }

    public String getPositivePeptides() {
        return positivePeptides;
    }

    public String getNegativePeptides() {
        return negativePeptides;
    }

    public void setNegativePeptides(String negativePeptides) {
        this.negativePeptides = negativePeptides;
    }

    public String getVectorJSON() {
        return vector.getJSON();
    }
}
