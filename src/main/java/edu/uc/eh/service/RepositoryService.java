package edu.uc.eh.service;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.GctReplicate;
import edu.uc.eh.domain.GctFile;
import edu.uc.eh.domain.PeakArea;
import edu.uc.eh.domain.PeptideAnnotation;
import edu.uc.eh.domain.ReplicateAnnotation;
import edu.uc.eh.domain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chojnasm on 8/11/15.
 */

@Service
public class RepositoryService {

    private static final Logger log = LoggerFactory.getLogger(RepositoryService.class);
    private final GctFileRepository gctFileRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public RepositoryService(GctFileRepository gctFileRepository,
                             PeakAreaRepository peakAreaRepository,
                             PeptideAnnotationRepository peptideAnnotationRepository,
                             ReplicateAnnotationRepository replicateAnnotationRepository,
                             ProfileRepository profileRepository) {
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.profileRepository = profileRepository;
    }

    public Set<GctReplicate> getGctReplicatesCombinations() {

        log.info("Get gct - replicate combinations");

        Set<GctReplicate> gctReplicatePairs = new HashSet<>();

        for (PeakArea peakArea : peakAreaRepository.findAll()) {
            GctFile gctFile = peakArea.getGctFile();
            ReplicateAnnotation replicateAnnotation = peakArea.getReplicateAnnotation();
            GctReplicate gctReplicate = new GctReplicate(gctFile, replicateAnnotation);

            if (!gctReplicatePairs.contains(gctReplicate)) {
                gctReplicatePairs.add(gctReplicate);
            }
        }
        return gctReplicatePairs;
    }

    public List<String> getReferenceProfileVector(AssayType assayType) {

        log.info("Get reference ProfileVector for assay type: {}", assayType);

        List<String> output = new ArrayList<>();
        for (PeptideAnnotation peptideAnnotation : peptideAnnotationRepository.findByAssayType(assayType)) {
            output.add(peptideAnnotation.getPeptideId());
        }
        Collections.sort(output);
        return output;
    }
}
