package edu.uc.eh.domain;

import edu.uc.eh.utils.ListWrapper;
import edu.uc.eh.utils.Tuples;

import javax.persistence.*;
import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */

@Entity
public class Profile{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private ReplicateAnnotation replicateAnnotation;

    @Lob
    private ListWrapper vector;

//    @ElementCollection(targetClass=Tuples.Tuple2.class)
//    private List<Tuples.Tuple2<String,Double>> positiveCorrelation;
//
//    @ElementCollection(targetClass=Tuples.Tuple2.class)
//    private List<Tuples.Tuple2<String,Double>> negativeCorrelation;

    public Profile(ReplicateAnnotation replicateAnnotation, List<Tuples.Tuple2<java.lang.String, Double>> vector) {

        this.replicateAnnotation = replicateAnnotation;
        this.vector = new ListWrapper(vector);
    }

    public Profile() {}

    @Override
    public java.lang.String toString() {
        return "Profile{" +
                "id=" + id +
                ", string=" + replicateAnnotation +
                ", vector=" + vector +
//                ", positiveCorrelation=" + positiveCorrelation +
//                ", negativeCorrelation=" + negativeCorrelation +
                '}';
    }

    public Long getId() {
        return id;
    }

    public ReplicateAnnotation getReplicateAnnotation() {
        return replicateAnnotation;
    }

    public List<Tuples.Tuple2<String, Double>> getVector() {
        return vector.getList();
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
