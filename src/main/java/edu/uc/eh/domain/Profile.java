package edu.uc.eh.domain;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.ListAndJsonWrapper2;
import edu.uc.eh.datatypes.ListWrapper;
import org.apache.commons.math3.stat.StatUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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


    @ManyToOne
    private MergedProfile mergedProfile;

    private AssayType assayType;

    @Lob
    private ListAndJsonWrapper2 vector;

    @Lob
    private ListAndJsonWrapper2 correlatedVector;

    private String positiveCorrelation;

    @Lob
    private String positivePeptides;

    @Lob
    private ListWrapper colors;
    private Integer clusteringOrder;

    private String concat;

    public Profile(ReplicateAnnotation replicateAnnotation, GctFile gctFile,
                   double[] vector,
                   boolean[] imputes,
                   List<String> referenceProfile,
                   int clusteringOrder) {

        this.replicateAnnotation = replicateAnnotation;
        this.gctFile = gctFile;
        this.vector = new ListAndJsonWrapper2(vector, imputes, referenceProfile);
        this.assayType = gctFile.getAssayType();

        this.colors = new ListWrapper(new int[vector.length]);

        double[] percentiles = new double[19];
        for (int i = 0; i < 19; i++) {
            percentiles[i] = StatUtils.percentile(vector, 5 * (i + 1));
        }

        for (int columnIndex = 0; columnIndex < vector.length; columnIndex++) {
            for (int j = 0; j < percentiles.length; j++) {
                if (vector[columnIndex] < percentiles[j]) {
                    colors.getList()[columnIndex] = j;
                    break;
                }
            }
        }
        this.clusteringOrder = clusteringOrder;

        this.concat = replicateAnnotation.getCellId() + replicateAnnotation.getPertiname() + assayType + getRunId();
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

    public ListAndJsonWrapper2 getCorrelatedVector() {
        return correlatedVector;
    }

    public void setCorrelatedVector(ListAndJsonWrapper2 correlatedVector) {
        this.correlatedVector = correlatedVector;
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

    public ListAndJsonWrapper2 getListWrapper() {
        return vector;
    }

    public String getPositivePeptides() {
        return positivePeptides;
    }

    public void setPositivePeptides(String positivePeptides) {
        this.positivePeptides = positivePeptides;
    }

    public String getVectorJSON() {
        return vector.getJSON();
    }

    public String getCorrelatedVectorJSON() {
        return correlatedVector.getJSON();
    }

    public int[] getColors() {
        return colors.getList();
    }

    public void setColors(ListWrapper colors) {
        this.colors = colors;
    }


    public Integer getClusteringOrder() {
        return clusteringOrder;
    }

    public void setClusteringOrder(Integer clusteringOrder) {
        this.clusteringOrder = clusteringOrder;
    }

    public String getConcat() {
        return concat;
    }

    public GctFile getGctFile() {
        return gctFile;
    }
}
