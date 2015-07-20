package edu.uc.eh.domain;

/**
 * Created by chojnasm on 7/17/15.
 */

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by chojnasm on 7/14/15.
 */
@Entity
public class GctFile {

    @Id
    @GeneratedValue
    private Long id;

    private String downloadUrl;

    @Enumerated(EnumType.STRING)
    private AssayType assayType;

    private int runId;
    private String runIdUrl;
    private Date processingDate;

    @OneToMany(mappedBy = "gctFile")
    private Set<PeakArea> peakAreas = new HashSet<>();

    GctFile(){}

    public GctFile(String DownloadUrl) {

        this.downloadUrl = DownloadUrl;
        this.assayType = DownloadUrl.contains("GCP") ? AssayType.GCP : AssayType.P100;
        this.processingDate = new Date();

    }

    @Override
    public String toString() {
        return "GctFile{" +
                "processingDate=" + processingDate +
                ", runId=" + runId +
                ", assayType=" + assayType +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", id=" + id +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getRunIdUrl() {
        return runIdUrl;
    }

    public void setRunIdUrl(String runIdUrl) {
        this.runIdUrl = runIdUrl;
    }

    public AssayType getAssayType() {
        return assayType;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public Set<PeakArea> getPeakAreas() {
        return peakAreas;
    }
}
