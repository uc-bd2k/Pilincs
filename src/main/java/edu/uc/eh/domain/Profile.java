package edu.uc.eh.domain;

import edu.uc.eh.utils.AssayType;
import edu.uc.eh.utils.ListWrapper;
import edu.uc.eh.utils.Tuples;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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

    public Profile(ReplicateAnnotation replicateAnnotation, AssayType assayType,
                   List<Tuples.Tuple2<String, Double>> vector) {

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

    public List<Tuples.Tuple2<String, Double>> getVector() {
        return vector.getList();
    }

    public double[] getVectorDoubles() {
        double[] doubles = new double[vector.getList().size()];
        for(int i=0; i<doubles.length; i++){
            Tuples.Tuple2 tuple2 = vector.getList().get(i);
            doubles[i]=((Double) tuple2.getT2());
        }
        return doubles;
    }


//    public List<Tuples.Tuple2<String, Double>> getPositiveCorrelation() {
//        return positiveCorrelation;
//    }
//
//    public void setPositiveCorrelation(List<Tuples.Tuple2<String, Double>> positiveCorrelation) {
//        this.positiveCorrelation = positiveCorrelation;
//    }
//
//    public List<Tuples.Tuple2<String, Double>> getNegativeCorrelation() {
//        return negativeCorrelation;
//    }
//
//    public void setNegativeCorrelation(List<Tuples.Tuple2<String, Double>> negativeCorrelation) {
//        this.negativeCorrelation = negativeCorrelation;
//    }
}
