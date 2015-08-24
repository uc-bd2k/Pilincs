package edu.uc.eh.domain;

import edu.uc.eh.utils.ConnectPanorama;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

/**
 * Created by chojnasm on 7/14/15.
 */
@Entity
public class PeakArea {

    @Autowired
    @Transient
    ConnectPanorama connectPanorama;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private GctFile gctFile;

    @ManyToOne
    private PeptideAnnotation peptideAnnotation;

    @ManyToOne
    private ReplicateAnnotation replicateAnnotation;

    private String chromatogramUrl;
    private Double value;

    private PeakArea() {
    }

    public PeakArea(GctFile gctfile, PeptideAnnotation peptideAnnotation,
                    ReplicateAnnotation replicateAnnotation,
                    Double value, String chromatogramUrl) {
        this.gctFile = gctfile;
        this.peptideAnnotation = peptideAnnotation;
        this.replicateAnnotation = replicateAnnotation;
        this.value = value;
        this.chromatogramUrl = chromatogramUrl;
    }

    @Override
    public String toString() {
        return "PeakArea{" +
                "value=" + value +
                ", chromatogramUrl='" + chromatogramUrl + '\'' +
                ", replicateAnnotation=" + replicateAnnotation +
                ", peptideAnnotation=" + peptideAnnotation +
                ", gctFile=" + gctFile +
                ", id=" + id +
                '}';
    }

    public Long getId() {
        return id;
    }

    public GctFile getGctFile() {
        return gctFile;
    }

    public PeptideAnnotation getPeptideAnnotation() {
        return peptideAnnotation;
    }

    public ReplicateAnnotation getReplicateAnnotation() {
        return replicateAnnotation;
    }

    public String getChromatogramUrl() {
        return chromatogramUrl;
    }

    public void setChromatogramUrl(String chromatogramUrl) {
        this.chromatogramUrl = chromatogramUrl;
    }

    public Double getValue() {
        return value;
    }
}
