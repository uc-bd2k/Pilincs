package edu.uc.eh.domain;

import edu.uc.eh.datatypes.AnnotationNameValue;
import edu.uc.eh.datatypes.AssayType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private String prBasePeptide;
    private String prCluster;
    private String prGeneClusterCode;
    private String prModifiedPeptideCode;
    private String prOriginalProbeId;
    private String prPhosphosite;
    private String prUniprotId;

    private String prHistoneMark;
    private String prBiNumber;



    @OneToMany(mappedBy = "peptideAnnotation")
    private Set<PeakArea> peakAreas = new HashSet<>();

    PeptideAnnotation(){}

    public PeptideAnnotation(AssayType assayType) {
        this.assayType = assayType;
    }

    public PeptideAnnotation(String peptideId, AssayType assayType) {
        this.peptideId = peptideId;
        this.assayType = assayType;
    }

    public PeptideAnnotation(AssayType assayType, List<AnnotationNameValue> annotationList) {

    }

    public static List<String> getAnnotationLabels(AssayType assayType) {
        List<String> output = new ArrayList<>();

//        output.add("peptideId");
        output.add("prGeneId");
        output.add("prGeneSymbol");
        output.add("prBasePeptide");
        if (assayType.equals(AssayType.P100)) {
            output.add("prCluster");
            output.add("prGeneClusterCode");

            output.add("prOriginalProbeId");
            output.add("prPhosphosite");
        }
        output.add("prModifiedPeptideCode");
        output.add("prUniprotId");
        if (assayType.equals(AssayType.GCP)) {
            output.add("prHistoneMark");
            output.add("prBiNumber");
        }
        return output;
    }

    @Override
    public String toString() {
        return "PeptideAnnotation{" +
                "id=" + id +
                ", assayType=" + assayType +
                ", peptideId='" + peptideId + '\'' +
                ", prGeneId='" + prGeneId + '\'' +
                ", prGeneSymbol='" + prGeneSymbol + '\'' +
                ", prBasePeptide='" + prBasePeptide + '\'' +
                ", prCluster='" + prCluster + '\'' +
                ", prGeneClusterCode='" + prGeneClusterCode + '\'' +
                ", prModifiedPeptideCode='" + prModifiedPeptideCode + '\'' +
                ", prOriginalProbeId='" + prOriginalProbeId + '\'' +
                ", prPhosphosite='" + prPhosphosite + '\'' +
                ", prUniprotId='" + prUniprotId + '\'' +
                ", prHistoneMark='" + prHistoneMark + '\'' +
                ", prBiNumber='" + prBiNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeptideAnnotation)) return false;

        PeptideAnnotation that = (PeptideAnnotation) o;

        if (getAssayType() != that.getAssayType()) return false;
        if (!getPeptideId().equals(that.getPeptideId())) return false;
        if (getPrGeneId() != null ? !getPrGeneId().equals(that.getPrGeneId()) : that.getPrGeneId() != null)
            return false;
        if (getPrGeneSymbol() != null ? !getPrGeneSymbol().equals(that.getPrGeneSymbol()) : that.getPrGeneSymbol() != null)
            return false;
        if (getPrBasePeptide() != null ? !getPrBasePeptide().equals(that.getPrBasePeptide()) : that.getPrBasePeptide() != null)
            return false;
        if (getPrCluster() != null ? !getPrCluster().equals(that.getPrCluster()) : that.getPrCluster() != null)
            return false;
        if (getPrGeneClusterCode() != null ? !getPrGeneClusterCode().equals(that.getPrGeneClusterCode()) : that.getPrGeneClusterCode() != null)
            return false;
        if (getPrModifiedPeptideCode() != null ? !getPrModifiedPeptideCode().equals(that.getPrModifiedPeptideCode()) : that.getPrModifiedPeptideCode() != null)
            return false;
        if (getPrOriginalProbeId() != null ? !getPrOriginalProbeId().equals(that.getPrOriginalProbeId()) : that.getPrOriginalProbeId() != null)
            return false;
        if (getPrPhosphosite() != null ? !getPrPhosphosite().equals(that.getPrPhosphosite()) : that.getPrPhosphosite() != null)
            return false;
        if (getPrUniprotId() != null ? !getPrUniprotId().equals(that.getPrUniprotId()) : that.getPrUniprotId() != null)
            return false;
        if (getPrHistoneMark() != null ? !getPrHistoneMark().equals(that.getPrHistoneMark()) : that.getPrHistoneMark() != null)
            return false;
        return !(getPrBiNumber() != null ? !getPrBiNumber().equals(that.getPrBiNumber()) : that.getPrBiNumber() != null);

    }

    @Override
    public int hashCode() {
        return getPeptideId().hashCode();
    }

    public Long getId() {
        return id;
    }

    public String getPeptideId() {
        return peptideId;
    }

    public void setPeptideId(String peptideId) {
        this.peptideId = peptideId;
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

//    public String escapedPeptideId() {
//
//            return peptideId.replace("+","%2B");
//
//    }

    public String getPrModifiedPeptideCode() {
        return prModifiedPeptideCode;
    }

    public void setPrModifiedPeptideCode(String prModifiedPeptideCode) {
        this.prModifiedPeptideCode = prModifiedPeptideCode;
    }

    public AssayType getAssayType() {
        return assayType;
    }

    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    public String getPrGeneClusterCode() {
        return prGeneClusterCode;
    }

    public void setPrGeneClusterCode(String prGeneClusterCode) {
        this.prGeneClusterCode = prGeneClusterCode;
    }

    public String getPrOriginalProbeId() {
        return prOriginalProbeId;
    }

    public void setPrOriginalProbeId(String prOriginalProbeId) {
        this.prOriginalProbeId = prOriginalProbeId;
    }

    public String getPrPhosphosite() {
        return prPhosphosite;
    }

    public void setPrPhosphosite(String prPhosphosite) {
        this.prPhosphosite = prPhosphosite;
    }

    public String getPrBiNumber() {
        return prBiNumber;
    }

    public void setPrBiNumber(String prBiNumber) {
        this.prBiNumber = prBiNumber;
    }

    public List<String> getAnnotationsForGct(AssayType assayType) {
        List<String> output = new ArrayList<>();

//        output.add(peptideId);
        output.add(prGeneId);
        output.add(prGeneSymbol);
        output.add(prBasePeptide);
        if (assayType.equals(AssayType.P100)) {
            output.add(prCluster);
            output.add(prGeneClusterCode);

            output.add(prOriginalProbeId);
            output.add(prPhosphosite);
        }
        output.add(prModifiedPeptideCode);
        output.add(prUniprotId);
        if (assayType.equals(AssayType.GCP)) {
            output.add(prHistoneMark);
            output.add(prBiNumber);
        }
        return output;
    }
}
