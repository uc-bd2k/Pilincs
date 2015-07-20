package edu.uc.eh.domain;

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
public class ReplicateAnnotation {

    @Id
    @GeneratedValue
    private Long id;

    private String replicateId;
    private String cellId;
    private String detPlate;
    private String detWell;
    private String pertDose;
    private String pertId;
    private String pertIname;
    private String pertTime;
    private String pertType;
    private String pertVehicle;
    private String pubchemCid;

    @OneToMany(mappedBy = "replicateAnnotation")
    private Set<PeakArea> peakAreas = new HashSet<>();

    ReplicateAnnotation(){}

    public ReplicateAnnotation(String replicateId) {
        this.replicateId = replicateId;
    }

    @Override
    public String toString() {
        return "ReplicateAnnotation{" +
                "pubchemCid='" + pubchemCid + '\'' +
                ", pertVehicle='" + pertVehicle + '\'' +
                ", pertType='" + pertType + '\'' +
                ", pertTime='" + pertTime + '\'' +
                ", pertIname='" + pertIname + '\'' +
                ", pertId='" + pertId + '\'' +
                ", pertDose='" + pertDose + '\'' +
                ", detWell='" + detWell + '\'' +
                ", detPlate='" + detPlate + '\'' +
                ", cellId='" + cellId + '\'' +
                ", replicateId='" + replicateId + '\'' +
                ", id=" + id +
                '}';
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


}
