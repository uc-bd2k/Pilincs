package edu.uc.eh.domain;

import edu.uc.eh.datatypes.AssayType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chojnasm on 7/17/15.
 */

@Entity
public class PeptideAnnotation {

    @Id
    @GeneratedValue
    private Long id;

    private AssayType assayType;
    private String peptideId;
    private String prGeneId;
    private String prGeneSymbol;
    private String prCluster;
    private String prUniprotId;
    private String prBasePeptide;
    private String prHistoneMark;
    private String prModifiedPeptideCode;


    @OneToMany(mappedBy = "peptideAnnotation")
    private Set<PeakArea> peakAreas = new HashSet<>();

    PeptideAnnotation(){}

    public PeptideAnnotation(String peptideId, AssayType assayType) {
        this.peptideId = peptideId;
        this.assayType = assayType;
    }

    @Override
    public String toString() {
        return "PeptideAnnotation{" +
                "prModifiedPeptideCode='" + prModifiedPeptideCode + '\'' +
                ", prHistoneMark='" + prHistoneMark + '\'' +
                ", prBasePeptide='" + prBasePeptide + '\'' +
                ", prUniprotId='" + prUniprotId + '\'' +
                ", prCluster='" + prCluster + '\'' +
                ", prGeneSymbol='" + prGeneSymbol + '\'' +
                ", prGeneId='" + prGeneId + '\'' +
                ", peptideId='" + peptideId + '\'' +
                ", id=" + id +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getPeptideId() {
        return peptideId;
    }

    public String getPrGeneId() {
        return prGeneId;
    }

    public void setPrGeneId(String prGeneId) {
        this.prGeneId = prGeneId;
    }

    public String getPrGeneSymbol() {
        return prGeneSymbol;
    }

    public void setPrGeneSymbol(String prGeneSymbol) {
        this.prGeneSymbol = prGeneSymbol;
    }

    public String getPrCluster() {
        return prCluster;
    }

    public void setPrCluster(String prCluster) {
        this.prCluster = prCluster;
    }

    public String getPrUniprotId() {
        return prUniprotId;
    }

    public void setPrUniprotId(String prUniprotId) {
        this.prUniprotId = prUniprotId;
    }

    public Set<PeakArea> getPeakAreas() {
        return peakAreas;
    }

    public String getPrBasePeptide() {
        return prBasePeptide;
    }

    public void setPrBasePeptide(String prBasePeptide) {
        this.prBasePeptide = prBasePeptide;
    }

    public String getPrHistoneMark() {
        return prHistoneMark;
    }

    public void setPrHistoneMark(String prHistoneMark) {
        this.prHistoneMark = prHistoneMark;
    }

    public String getPrModifiedPeptideCode() {
        return prModifiedPeptideCode;
    }

    public void setPrModifiedPeptideCode(String prModifiedPeptideCode) {
        this.prModifiedPeptideCode = prModifiedPeptideCode;
    }

    public String escapedPeptideId() {

            return peptideId.replace("+","%2B");

    }

    public AssayType getAssayType() {
        return assayType;
    }

    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }
}
