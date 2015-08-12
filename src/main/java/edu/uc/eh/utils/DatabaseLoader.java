package edu.uc.eh.utils;

import edu.uc.eh.datatypes.GctReplicate;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.*;
import edu.uc.eh.domain.repository.*;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.service.RepositoryService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.labkey.remoteapi.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by chojnasm on 7/14/15.
 */

@Service
public class DatabaseLoader {

    private List<String> referenceP100Profile;
    private List<String> referenceGCPProfile;

    private final ConnectPanorama connectPanorama;
    private final ParseGCT parser;
    private final GctFileRepository gctFileRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final ProfileRepository profileRepository;

    private final RepositoryService repositoryService;

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(ConnectPanorama connectPanorama,
                          ParseGCT parser,
                          GctFileRepository gctFileRepository,
                          PeakAreaRepository peakAreaRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository,
                          ProfileRepository profileRepository,
                          RepositoryService repositoryService
                          ) {
        this.connectPanorama = connectPanorama;
        this.parser = parser;
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.profileRepository = profileRepository;
        this.repositoryService = repositoryService;
    }

    @PostConstruct
    private void initDatabase() throws Exception {

        loadPeptideAnnotations();

        referenceP100Profile = repositoryService.getReferenceProfileVector(AssayType.P100);
        referenceGCPProfile = repositoryService.getReferenceProfileVector(AssayType.GCP);

        loadRawData();
        loadProfiles();
        loadCorrelations();

        cleanUp();
    }

    private void cleanUp() {
//        TODO: free memory
    }

    private void loadPeptideAnnotations() throws Exception {
        log.info("Populating peptideAnnotations from panorama's Jsons, but only with keys");

        List<String> p100Peptides = connectPanorama.getPeptideReferenceIdNames(AssayType.P100);
        List<String> gcpPeptides = connectPanorama.getPeptideReferenceIdNames(AssayType.GCP);

        for(String peptide : p100Peptides){
            if(peptideAnnotationRepository.findByPeptideId(peptide).size() == 0) {
                PeptideAnnotation peptideAnnotation = new PeptideAnnotation(peptide, AssayType.P100);
                peptideAnnotationRepository.save(peptideAnnotation);
            }
        }

        for(String peptide : gcpPeptides){
            if(peptideAnnotationRepository.findByPeptideId(peptide).size() == 0) {
                PeptideAnnotation peptideAnnotation = new PeptideAnnotation(peptide, AssayType.GCP);
                peptideAnnotationRepository.save(peptideAnnotation);
            }
        }
    }


    private void loadRawData() throws IOException, CommandException {
        log.info("Loading raw data from panorama etc");

        List<String> list = connectPanorama.gctDownloadUrls(true);
        int counter = 0;

        for (String url : list) {
//            if(counter++ > 0) continue;
            HashMap<String, List<ParseGCT.AnnotationValue>> metaProbes = new HashMap<>();
            HashMap<String, List<ParseGCT.AnnotationValue>> metaReplicas = new HashMap<>();
            ArrayList<ParseGCT.ProbeReplicatePeak> peakValues = new ArrayList<>();

            parser.parseToRepository(url, peakValues, metaProbes, metaReplicas);

            GctFile gctfile = new GctFile(url);
            gctfile.setRunId(UtilsParse.parseRunId(url));
            gctfile.setRunIdUrl(connectPanorama.getRunIdLink(gctfile));
            gctFileRepository.save(gctfile);

            List<String> probeNameIds = new ArrayList<>(metaProbes.keySet());
            HashMap<String, Integer> peptideIdsForChromatogramsUrl = connectPanorama.getPeptideIdsFromJSON(probeNameIds,
                    UtilsParse.parseArrayTypeFromUrl(url),
                    UtilsParse.parseRunId(url)
            );

            for (ParseGCT.ProbeReplicatePeak peak : peakValues) {

                String probeId = peak.getProbeId();
                String replicateId = peak.getReplicateId();

                PeptideAnnotation peptideAnnotation = peptideAnnotationRepository.findFirstByPeptideId(probeId);
                ReplicateAnnotation replicateAnnotation = replicateAnnotationRepository.findFirstByReplicateId(replicateId);

                Double peakAreaValue = peak.getPeakArea();

                // Dirty hack
                if (peptideAnnotation.getPrBasePeptide() == null) {

                    for (ParseGCT.AnnotationValue annotationObject : metaProbes.get(probeId)) {
                        String annotationName = annotationObject.getAnnotationName();
                        String annotationValue = annotationObject.getAnnotationValue();

                        switch (annotationName) {
                            case "pr_gene_id":
                                peptideAnnotation.setPrGeneId(annotationValue);
                                break;
                            case "pr_gene_symbol":
                            case "GeneName":
                                peptideAnnotation.setPrGeneSymbol(annotationValue);
                                break;
                            case "pr_p100_cluster":
                            case "pr_gcp_cluster":
                                peptideAnnotation.setPrCluster(annotationValue);
                                break;
                            case "pr_uniprot_id":
                                peptideAnnotation.setPrUniprotId(annotationValue);
                                break;
                            case "pr_p100_base_peptide":
                            case "pr_gcp_base_peptide":
                                peptideAnnotation.setPrBasePeptide(annotationValue);
                                break;
                            case "pr_gcp_histone_mark":
                                peptideAnnotation.setPrHistoneMark(annotationValue);
                                break;
                            case "pr_gcp_modified_peptide_code":
                            case "pr_p100_modified_peptide_code":
                                peptideAnnotation.setPrModifiedPeptideCode(annotationValue);
                                break;
                            case "pr_probe_suitability_manual":
                                if(annotationValue.equals("FALSE")) {
                                    peakAreaValue = null;
                                }
                                break;
                            default:
                        }
                    }
                    peptideAnnotationRepository.save(peptideAnnotation);
                }

                if (replicateAnnotation == null) {
                    replicateAnnotation = new ReplicateAnnotation(replicateId);
                    for (ParseGCT.AnnotationValue annotationObject : metaReplicas.get(replicateId)) {
                        String annotationName = annotationObject.getAnnotationName();
                        String annotationValue = annotationObject.getAnnotationValue();

                        switch (annotationName) {
                            case "cell_id":
                                replicateAnnotation.setCellId(annotationValue);
                                break;
                            case "det_plate":
                                replicateAnnotation.setDetPlate(annotationValue);
                                break;
                            case "det_well":
                                replicateAnnotation.setDetWell(annotationValue);
                                break;
                            case "pert_dose":
                                replicateAnnotation.setPertDose(annotationValue);
                                break;
                            case "pert_id":
                                replicateAnnotation.setPertId(annotationValue);
                                break;
                            case "pert_iname":
                                replicateAnnotation.setPertiname(annotationValue);
                                break;
                            case "pert_time":
                                replicateAnnotation.setPertTime(annotationValue);
                                break;
                            case "pert_type":
                                replicateAnnotation.setPertType(annotationValue);
                                break;
                            case "pert_vehicle":
                                replicateAnnotation.setPertVehicle(annotationValue);
                            case "pubchem_cid":
                                replicateAnnotation.setPubchemCid(annotationValue);
                                break;
                            default:
//                                log.warn("New annotation: {}",annotationName);
                        }
                    }
                    replicateAnnotationRepository.save(replicateAnnotation);
                }

                PeakArea peakArea = new PeakArea(gctfile, peptideAnnotation, replicateAnnotation, peakAreaValue);

                peakArea.setChromatogramsUrl(connectPanorama.getChromatogramsUrl(
                        UtilsParse.parseArrayTypeFromUrl(url),
                        peptideIdsForChromatogramsUrl.get(peakArea.getPeptideAnnotation().getPeptideId()),
                        peakArea.getReplicateAnnotation().getReplicateId()));

                peakAreaRepository.save(peakArea);

            }
        }
    }


    private void loadProfiles() {
        log.info("Filling repository with profiles");

        List<String> referenceProfile = null;

        Set<GctReplicate> gctReplicatePairs = repositoryService.getGctReplicatesCombinations();

        for (GctReplicate gctReplicate : gctReplicatePairs) {

            GctFile gctFile = gctReplicate.getGctFile();
            ReplicateAnnotation replicateAnnotation = gctReplicate.getReplicateAnnotation();

            List<PeakArea> peakAreas = peakAreaRepository.findByGctFileAndReplicateAnnotation(gctFile, replicateAnnotation);

            AssayType assayType = gctFile.getAssayType();

            referenceProfile = getReferenceProfile(assayType);

            Double[] profileVector = new Double[referenceProfile.size()];
            boolean[] imputeVector = new boolean[referenceProfile.size()];

            int index;

            for (PeakArea peakArea : peakAreas) {
                index = referenceProfile.indexOf(peakArea.getPeptideAnnotation().getPeptideId());
                profileVector[index] = peakArea.getValue();
            }

            UtilsStatistics.imputeProfileVector(profileVector, imputeVector);

            Profile profile = new Profile(
                    replicateAnnotation,
                    gctFile,
                    ArrayUtils.toPrimitive(profileVector),
                    imputeVector,
                    referenceProfile);

            profileRepository.save(profile);
        }
    }


    private void loadCorrelations(){
        log.info("Filling repository with most correlated profiles");

        List<String> referenceProfile = null;

        for (AssayType assayType : AssayType.values()) {
            referenceProfile = getReferenceProfile(assayType);
            List<Profile> profiles = profileRepository.findByAssayType(assayType);

            for (Profile profileA : profiles) {
//                double[] doublesA = profileA.getVectorDoubles();
                Double maxPearson = Double.MIN_VALUE;

                Profile maxProfile = profileA;

                for (Profile profileB : profiles) {
                    if (profileA.equals(profileB)) continue;

                    double[] vectorA = profileA.getVector();
                    double[] vectorB = profileB.getVector();

                    PearsonsCorrelation pearson = new PearsonsCorrelation();
                    Double pearsonCorrelation = pearson.correlation(vectorA,vectorB);

                    if (pearsonCorrelation >= maxPearson) {
                        maxPearson = pearsonCorrelation;
                        maxProfile = profileB;
                    }
                }

                profileA.setCorrelatedVector(maxProfile.getListWrapper());

                SortedSet<StringDouble> positivePeptides = UtilsStatistics.influentialPeptides(
                        profileA.getVector(), maxProfile.getVector(), referenceProfile, true);

                profileA.setPositivePeptides(UtilsTransform.SortedSetToHTML(positivePeptides, false));

                DecimalFormat df = new DecimalFormat("0.0000");
                String peptideCorrelation = " <br/><br/><b style=\"color: #23527c;\">%s</b>";

                profileA.setPositiveCorrelation(maxProfile.toString() + String.format(peptideCorrelation, df.format(maxPearson)));

                profileRepository.save(profileA);
            }
        }
    }



    public List<String> getReferenceProfile(AssayType assayType){
        if(assayType.equals(AssayType.GCP)){
            return referenceGCPProfile;
        }else{
            return referenceP100Profile;
        }
    }
}
