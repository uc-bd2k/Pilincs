package edu.uc.eh.domain.json;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.domain.PeakArea;

import java.util.Date;

/**
 * Created by chojnasm on 7/20/15.
 */
public class RawDataRecord {

    // From PeakArea
    private String chromatogramsUrl;
    private Double value;

    // From GctFile
    private String downloadUrl;
    private AssayType assayType;
    private int runId;
    private String runIdUrl;
    private Date processingDate;

    // From PeptideAnnotation
    private String peptideId;
    private String prGeneId;
    private String prGeneSymbol;
    private String prCluster;
    private String prUniprotId;
    private String prBasePeptide;
    private String prHistoneMark;
    private String prModifiedPeptideCode;

    // From ReplicateAnnotation
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

    public RawDataRecord(PeakArea peakArea) {
        this.chromatogramsUrl = "<a href=\"" + peakArea.getChromatogramsUrl() + "\" target=\"_blank\" \"><img src=\"https://panoramaweb.org/labkey/TargetedMS/images/TransitionGroupLib.gif\"></a>";
        this.value=peakArea.getValue();
        this.downloadUrl= "<a href=\"" + peakArea.getGctFile().getDownloadUrl() + "\" target=\"_blank\"\">gct</a>";
        this.assayType=peakArea.getGctFile().getAssayType();
        this.runId=peakArea.getGctFile().getRunId();
        this.runIdUrl= "<a href=\"" +peakArea.getGctFile().getRunIdUrl() + "\" target=\"_blank\" \"> RunId: "+this.runId+"</a>";
        this.processingDate=peakArea.getGctFile().getProcessingDate();
        this.peptideId=peakArea.getPeptideAnnotation().getPeptideId();
        this.prGeneId=peakArea.getPeptideAnnotation().getPrGeneId();
        this.prGeneSymbol=peakArea.getPeptideAnnotation().getPrGeneSymbol();
        this.prCluster=peakArea.getPeptideAnnotation().getPrCluster();
        this.prUniprotId=peakArea.getPeptideAnnotation().getPrUniprotId();
        this.prBasePeptide=peakArea.getPeptideAnnotation().getPrBasePeptide();
        this.prHistoneMark=peakArea.getPeptideAnnotation().getPrHistoneMark();
        this.prModifiedPeptideCode=peakArea.getPeptideAnnotation().getPrModifiedPeptideCode();
        this.replicateId=peakArea.getReplicateAnnotation().getReplicateId();
        this.cellId=peakArea.getReplicateAnnotation().getCellId();
        this.detPlate=peakArea.getReplicateAnnotation().getDetPlate();
        this.detWell=peakArea.getReplicateAnnotation().getDetWell();
        this.pertDose=peakArea.getReplicateAnnotation().getPertDose();
        this.pertId=peakArea.getReplicateAnnotation().getPertId();
        this.pertIname=peakArea.getReplicateAnnotation().getPertiname();
        this.pertTime=peakArea.getReplicateAnnotation().getPertTime();
        this.pertType=peakArea.getReplicateAnnotation().getPertType();
        this.pertVehicle=peakArea.getReplicateAnnotation().getPertVehicle();
        this.pubchemCid=peakArea.getReplicateAnnotation().getPubchemCid();
    }

    public String getChromatogramsUrl() {
        return chromatogramsUrl;
    }

    public void setChromatogramsUrl(String chromatogramsUrl) {
        this.chromatogramsUrl = chromatogramsUrl;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public AssayType getAssayType() {
        return assayType;
    }

    public void setAssayType(AssayType assayType) {
        this.assayType = assayType;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getRunIdUrl() {
        return runIdUrl;
    }

    public void setRunIdUrl(String runIdUrl) {
        this.runIdUrl = runIdUrl;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
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
}
