package edu.uc.eh.service;

import edu.uc.eh.domain.*;
import edu.uc.eh.domain.repository.GctFileRepository;
import edu.uc.eh.domain.repository.PeakAreaRepository;

import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chojnasm on 7/14/15.
 */

@Service
public class DatabaseLoader {

    private final GctFileRepository gctFileRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;

    @Autowired
    public DatabaseLoader(GctFileRepository gctFileRepository,
                          PeakAreaRepository peakAreaRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository) {
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
    }

    @Autowired
    ConnectPanorama connectPanorama;

    @PostConstruct
    private void initDatabase() {
        List<String> list = Arrays.asList("https://panoramaweb.org/labkey/targetedms/LINCS/P100/RunGCTReport.view?runId=2108&reportName=GCT%20File%20P100&processed=true",
                "https://panoramaweb.org/labkey/targetedms/LINCS/GCP/RunGCTReport.view?runId=2144&reportName=GCT%20File%20GCP&processed=true");

        for(String a : list) {
            GctFile gctfile = new GctFile(a);
            PeptideAnnotation pepAnn = null;
            ReplicateAnnotation repAnn = null;
            if (gctfile.getAssayType().equals(AssayType.GCP)) {
                gctfile.setRunId(2144);

                pepAnn = peptideAnnotationRepository.save(new PeptideAnnotation("BI10007"));
                repAnn = replicateAnnotationRepository.save(new ReplicateAnnotation("GM7-49453-003A03"));
            } else {
                gctfile.setRunId(2108);
                pepAnn = peptideAnnotationRepository.save(new PeptideAnnotation("10011_DYRK_Y321_IYQY[+80]IQSR"));
                repAnn = replicateAnnotationRepository.save(new ReplicateAnnotation("PA9-79EE-001A01"));
            }
            gctfile.setRunIdUrl(connectPanorama.getRunIdLink(gctfile));
            gctFileRepository.save(gctfile);


            PeakArea pa1 = new PeakArea(gctfile, pepAnn, repAnn, 34.2);
            PeakArea pa2 = new PeakArea(gctfile, pepAnn, repAnn, -15.1);

            pa1.setSourceUrl(connectPanorama.getSourceUrl(pa1));
            pa2.setSourceUrl(connectPanorama.getSourceUrl(pa2));

            peakAreaRepository.save(pa1);
            peakAreaRepository.save(pa2);
        }
    }

}
