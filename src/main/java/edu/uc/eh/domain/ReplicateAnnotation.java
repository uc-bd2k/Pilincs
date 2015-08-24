package edu.uc.eh.domain;

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
public class ReplicateAnnotation {

    @Id
    @GeneratedValue
    private Long id;

    private String replicateId;
    //    private String canonicalSmiles;
    private String cellId;
    //    private String cellReprogrammed;
//    private String detFilename;
//    private String det_normalization_group_vector;
    private String detPlate;
    private String detWell;
    private String isomericSmiles;
    //    private String pert_batch_internal_compound_enumerator;
//    private String pert_batch_internal_replicate;
    private String pertDose;
    private String pertDoseUnit;
    private String pertId;
    private String pertiname;
    private String pertTime;
    private String pertTimeUnit;
    private String pertType;
    private String pertVehicle;
    //    private String provenanceCode;
    private String pubchemCid;

    @OneToMany(mappedBy = "replicateAnnotation")
    private Set<PeakArea> peakAreas = new HashSet<>();

    @OneToMany(mappedBy = "replicateAnnotation")
    private Set<Profile> profiles = new HashSet<>();


    public ReplicateAnnotation() {
    }

    public ReplicateAnnotation(String replicateId) {
        this.replicateId = replicateId;
    }

    public static List<String> getAnnotationLabels() {
        List<String> output = new ArrayList<>();

//        output.add("replicateId");
        output.add("cellId");
        output.add("detPlate");
        output.add("detWell");
        output.add("isomericSmiles");
        output.add("pertDose");
        output.add("pertDoseUnit");
        output.add("pertId");
        output.add("pertiname");
        output.add("pertTime");
        output.add("pertTimeUnit");
        output.add("pertType");
        output.add("pertVehicle");
        output.add("pubchemCid");

        return output;
    }

    @Override
    public String toString() {
        return "ReplicateAnnotation{" +
                "pubchemCid='" + pubchemCid + '\'' +
                ", pertVehicle='" + pertVehicle + '\'' +
                ", pertType='" + pertType + '\'' +
                ", pertTime='" + pertTime + '\'' +
                ", pertiname='" + pertiname + '\'' +
                ", pertId='" + pertId + '\'' +
                ", pertDose='" + pertDose + '\'' +
                ", detWell='" + detWell + '\'' +
                ", detPlate='" + detPlate + '\'' +
                ", cellId='" + cellId + '\'' +
                ", replicateId='" + replicateId + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplicateAnnotation)) return false;

        ReplicateAnnotation that = (ReplicateAnnotation) o;

        if (getReplicateId() != null ? !getReplicateId().equals(that.getReplicateId()) : that.getReplicateId() != null)
            return false;
        if (getCellId() != null ? !getCellId().equals(that.getCellId()) : that.getCellId() != null) return false;
        if (getDetPlate() != null ? !getDetPlate().equals(that.getDetPlate()) : that.getDetPlate() != null)
            return false;
        if (getDetWell() != null ? !getDetWell().equals(that.getDetWell()) : that.getDetWell() != null) return false;
        if (getIsomericSmiles() != null ? !getIsomericSmiles().equals(that.getIsomericSmiles()) : that.getIsomericSmiles() != null)
            return false;
        if (getPertDose() != null ? !getPertDose().equals(that.getPertDose()) : that.getPertDose() != null)
            return false;
        if (getPertDoseUnit() != null ? !getPertDoseUnit().equals(that.getPertDoseUnit()) : that.getPertDoseUnit() != null)
            return false;
        if (getPertId() != null ? !getPertId().equals(that.getPertId()) : that.getPertId() != null) return false;
        if (getPertiname() != null ? !getPertiname().equals(that.getPertiname()) : that.getPertiname() != null)
            return false;
        if (getPertTime() != null ? !getPertTime().equals(that.getPertTime()) : that.getPertTime() != null)
            return false;
        if (getPertTimeUnit() != null ? !getPertTimeUnit().equals(that.getPertTimeUnit()) : that.getPertTimeUnit() != null)
            return false;
        if (getPertType() != null ? !getPertType().equals(that.getPertType()) : that.getPertType() != null)
            return false;
        if (getPertVehicle() != null ? !getPertVehicle().equals(that.getPertVehicle()) : that.getPertVehicle() != null)
            return false;
        return !(getPubchemCid() != null ? !getPubchemCid().equals(that.getPubchemCid()) : that.getPubchemCid() != null);

    }

    @Override
    public int hashCode() {
        return getReplicateId() != null ? getReplicateId().hashCode() : 0;
    }

    public Long getId() {
        return id;
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

    public String getDetPlate() {
        return detPlate;
    }

    public void setDetPlate(String detPlate) {
        this.detPlate = detPlate;
    }

    public String getDetWell() {
        return detWell;
    }

    public void setDetWell(String detWell) {
        this.detWell = detWell;
    }

    public String getPertDose() {
        return pertDose;
    }

    public void setPertDose(String pertDose) {
        this.pertDose = pertDose;
    }

    public String getPertId() {
        return pertId;
    }

    public void setPertId(String pertId) {
        this.pertId = pertId;
    }

    public String getPertiname() {
        return pertiname;
    }

    public void setPertiname(String pertiname) {
        this.pertiname = pertiname;
    }

    public String getPertTime() {
        return pertTime;
    }

    public void setPertTime(String pertTime) {
        this.pertTime = pertTime;
    }

    public String getPertType() {
        return pertType;
    }

    public void setPertType(String pertType) {
        this.pertType = pertType;
    }

    public String getPertVehicle() {
        return pertVehicle;
    }

    public void setPertVehicle(String pertVehicle) {
        this.pertVehicle = pertVehicle;
    }

    public String getPubchemCid() {
        return pubchemCid;
    }

    public void setPubchemCid(String pubchemCid) {
        this.pubchemCid = pubchemCid;
    }

    public Set<PeakArea> getPeakAreas() {
        return peakAreas;
    }

    public String getIsomericSmiles() {
        return isomericSmiles;
    }

    public void setIsomericSmiles(String isomericSmiles) {
        this.isomericSmiles = isomericSmiles;
    }

    public String getPertDoseUnit() {
        return pertDoseUnit;
    }

    public void setPertDoseUnit(String pertDoseUnit) {
        this.pertDoseUnit = pertDoseUnit;
    }

    public String getPertTimeUnit() {
        return pertTimeUnit;
    }

    public void setPertTimeUnit(String pertTimeUnit) {
        this.pertTimeUnit = pertTimeUnit;
    }

    public List<String> getAnnotationsForGct() {
        List<String> output = new ArrayList<>();

        output.add(replicateId);
        output.add(cellId);
        output.add(detPlate);
        output.add(detWell);
        output.add(isomericSmiles);
        output.add(pertDose);
        output.add(pertDoseUnit);
        output.add(pertId);
        output.add(pertiname);
        output.add(pertTime);
        output.add(pertTimeUnit);
        output.add(pertType);
        output.add(pertVehicle);
        output.add(pubchemCid);

        return output;
    }


}
