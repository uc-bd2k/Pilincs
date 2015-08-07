package edu.uc.eh.domain;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.ListWrapper;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.datatypes.Tuples;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */

@Entity
public class Profile implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private ReplicateAnnotation replicateAnnotation;

    private AssayType assayType;

    @Lob
    private ListWrapper vector;

    private String positiveCorrelation;
    private String negativeCorrelation;

    @Lob
    private String positivePeptides;

    @Lob
    private String negativePeptides;

    public Profile(ReplicateAnnotation replicateAnnotation, AssayType assayType,
                   List<StringDouble> vector) {

        this.replicateAnnotation = replicateAnnotation;
        this.assayType = assayType;
        this.vector = new ListWrapper(vector);
    }

    public Profile() {}

    @Override
    public String toString() {
        return  "<b>Assay: </b> " + getAssayType() +
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
        return assayType;
    }

    public List<StringDouble> getVector() {
        return vector.getList();
    }

    public double[] getVectorDoubles() {
        double[] doubles = new double[vector.getList().size()];
        for(int i=0; i<doubles.length; i++){
            StringDouble tuple2 = vector.getList().get(i);
            doubles[i]= tuple2.getaDouble();
        }
        return doubles;
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
}
