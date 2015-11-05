package edu.uc.eh.utils;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.GctReplicate;
import edu.uc.eh.datatypes.IdNameValue;
import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.*;
import edu.uc.eh.domain.repository.*;
import edu.uc.eh.service.PeptideService;
import edu.uc.eh.service.ReplicateService;
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

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);
    private final ConnectPanorama connectPanorama;
    private final ParseGCT parser;
    private final GctFileRepository gctFileRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final ProfileRepository profileRepository;
    private final MergedProfileRepository mergedProfileRepository;
    private final RepositoryService repositoryService;
    private final PeptideService peptideService;
    private final ReplicateService replicateService;

    private List<String> referenceP100Profile;
    private List<String> referenceGCPProfile;

    private List<String> referenceP100GeneNames;
    private List<String> referenceGCPGeneNames;


    @Autowired
    public DatabaseLoader(ConnectPanorama connectPanorama,
                          ParseGCT parser,
                          GctFileRepository gctFileRepository,
                          PeakAreaRepository peakAreaRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository,
                          ProfileRepository profileRepository,
                          MergedProfileRepository mergedProfileRepository,
                          RepositoryService repositoryService,
                          PeptideService peptideService,
                          ReplicateService replicateService
    ) {
        this.connectPanorama = connectPanorama;
        this.parser = parser;
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.profileRepository = profileRepository;
        this.mergedProfileRepository = mergedProfileRepository;
        this.repositoryService = repositoryService;
        this.peptideService = peptideService;
        this.replicateService = replicateService;
    }

    @PostConstruct
    private void initDatabase() throws Exception {

        loadPeptideAnnotations();

        referenceP100Profile = repositoryService.getReferenceProfileVector(AssayType.P100);
        referenceGCPProfile = repositoryService.getReferenceProfileVector(AssayType.GCP);

        referenceP100GeneNames = repositoryService.getReferenceGeneNames(AssayType.P100);
        referenceGCPGeneNames = repositoryService.getReferenceGeneNames(AssayType.GCP);

        loadReplicateAnnotations();

        loadRawData();
        buildProfiles();
        computeCorrelations();
        mergeProfiles(referenceP100Profile.size(), referenceGCPProfile.size());
//        runHierarchicalClustering();

        cleanUp();
    }


    private void cleanUp() {
//        TODO:
    }

    private void loadPeptideAnnotations() throws Exception {
        log.info("Populating peptideAnnotations from panorama's Jsons");

        for (AssayType assayType : AssayType.values()) {

            String jsonUrl = connectPanorama.getPeptideJsonUrl(assayType);
            String jsonAsString = UtilsNetwork.readUrl(jsonUrl);
            List<IdNameValue> peptides = UtilsParse.getAnnotationsFromJSON(jsonAsString, "PeptideId");

            peptideService.parseAndSavePeptideAnnotations(peptides, assayType);
        }
    }

    private void loadReplicateAnnotations() throws Exception {
        log.info("Populating replicateAnnotations from panorama's Jsons");

        for (AssayType assayType : AssayType.values()) {
            String jsonUrl = connectPanorama.getReplicateJsonUrl(assayType);
            String jsonAsString = UtilsNetwork.readUrl(jsonUrl);
            List<IdNameValue> replicates = UtilsParse.getAnnotationsFromJSON(jsonAsString, "ReplicateId");

            replicateService.parseAndSaveReplicateAnnotations(replicates, assayType, jsonUrl);
        }
    }

    private void loadRawData() throws IOException, CommandException {
        log.info("Loading peak areas from panorama gct files");

        List<String> gctDownloadUrls = connectPanorama.gctDownloadUrls(true);
        int counter = 0;

        for (String url : gctDownloadUrls) {
//            if(counter++ > 0) continue;
            HashMap<String, List<ParseGCT.AnnotationValue>> metaPeptides = new HashMap<>();
            HashMap<String, List<ParseGCT.AnnotationValue>> metaReplicas = new HashMap<>();
            ArrayList<ParseGCT.PeptideReplicatePeak> peakValues = new ArrayList<>();

            try {
                parser.parseToRepository(url, peakValues, metaPeptides, metaReplicas);
            }catch(Exception e){

            }

            GctFile gctfile = new GctFile(url);

            int runId = UtilsParse.parseRunId(url);
            String runIdUrl = connectPanorama.getRunIdLink(gctfile);

            gctfile.setRunId(runId);
            gctfile.setRunIdUrl(runIdUrl);

            gctFileRepository.save(gctfile);

            List<String> probeNameIds = new ArrayList<>(metaPeptides.keySet());

            AssayType assayType = UtilsParse.parseArrayTypeFromUrl(url);

            HashMap<String, Integer> peptideIdsForChromatogramsUrl =
                    connectPanorama.getPeptideIdsFromJSON(probeNameIds, assayType, runId);


            for (ParseGCT.PeptideReplicatePeak peakFromGct : peakValues) {

                String peptideId = peakFromGct.getPeptideId();
                String replicateId = peakFromGct.getReplicateId();


                PeptideAnnotation peptideAnnotation = peptideAnnotationRepository.findFirstByPeptideId(peptideId);
                ReplicateAnnotation replicateAnnotation = replicateAnnotationRepository.findFirstByReplicateId(replicateId);

                if (peptideAnnotation == null) {
                    log.warn("Peptide annotation is null");
                }

                if (replicateAnnotation == null) {
                    log.warn("Replicate annotation is null");
                }

                Double peakAreaValue = peakFromGct.getPeakArea();

                for (ParseGCT.AnnotationValue annotationObject : metaPeptides.get(peptideId)) {
                    String annotationName = annotationObject.getAnnotationName();
                    String annotationValue = annotationObject.getAnnotationValue();

                    if (annotationName.equals("pr_probe_suitability_manual") &&
                            annotationValue.equals("FALSE")) {
                        peakAreaValue = null;
                    }
                }

                String chromatogramUrl = connectPanorama.getChromatogramUrl(
                        assayType, peptideIdsForChromatogramsUrl.get(peptideId), replicateId);

                PeakArea peakArea = new PeakArea(gctfile, peptideAnnotation, replicateAnnotation, peakAreaValue, chromatogramUrl);

                peakAreaRepository.save(peakArea);

            }
        }
    }


    private void buildProfiles() {
        log.info("Filling repository with profiles");

        List<String> referenceProfile;
        List<String> referenceGeneNames;

        Set<GctReplicate> gctReplicatePairs = repositoryService.getGctReplicatesCombinations();

        int dummyClusteringOrder = gctReplicatePairs.size();
        for (GctReplicate gctReplicate : gctReplicatePairs) {

            GctFile gctFile = gctReplicate.getGctFile();
            ReplicateAnnotation replicateAnnotation = gctReplicate.getReplicateAnnotation();

            List<PeakArea> peakAreas = peakAreaRepository.findByGctFileAndReplicateAnnotation(gctFile, replicateAnnotation);

            AssayType assayType = gctFile.getAssayType();

            referenceProfile = getReferenceProfile(assayType);
            referenceGeneNames = getReferenceGeneNames(assayType);

            Double[] profileVector = new Double[referenceProfile.size()];
            boolean[] imputeVector = new boolean[referenceProfile.size()];

            for (PeakArea peakArea : peakAreas) {
                int index = referenceProfile.indexOf(peakArea.getPeptideAnnotation().getPeptideId());
                profileVector[index] = peakArea.getValue();
            }

            UtilsStatistics.imputeProfileVector(profileVector, imputeVector);

            Profile profile = new Profile(
                    replicateAnnotation,
                    gctFile,
                    ArrayUtils.toPrimitive(profileVector),
                    imputeVector,
                    referenceProfile,
                    referenceGeneNames,
                    dummyClusteringOrder--);

            profileRepository.save(profile);
        }

    }

    private void mergeProfiles(int p100Length, int gcpLength) {

        log.info("Merging profiles");

        ArrayList<AssayType> dummyAssay = new ArrayList<>();
        dummyAssay.add(AssayType.GCP);
        dummyAssay.add(AssayType.P100);

        List<Profile> profiles = profileRepository.findByAssayTypeInOrderByConcatDesc(dummyAssay);

        String prevConcat = null;
        String curConcat;
        List<Profile> bunchOfProfiles = null;

        for (Profile profile : profiles) {
            curConcat = profile.getReplicateAnnotation().getCellId()
                    + profile.getReplicateAnnotation().getPertiname();
            if (prevConcat == null) {
                prevConcat = curConcat;
                bunchOfProfiles = new ArrayList<>();
            }

            if (!curConcat.equals(prevConcat)) {
                prevConcat = curConcat;

                MergedProfile mergedProfile = UtilsTransform.mergeProfiles(bunchOfProfiles, p100Length, gcpLength);
                mergedProfileRepository.save(mergedProfile);

                bunchOfProfiles = new ArrayList<>();

            } else {
                bunchOfProfiles.add(profile);
            }
        }

    }

    private void computeCorrelations() {
        log.info("Filling repository with most correlated profiles");

        List<String> referenceProfile;

        for (AssayType assayType : AssayType.values()) {


            referenceProfile = getReferenceProfile(assayType);
            List<Profile> profiles = profileRepository.findByAssayType(assayType);

            String[] profileNames = new String[profiles.size()];
            double[][] distanceMatrix = new double[profiles.size()][profiles.size()];

            int i = 0;

            for (Profile profileA : profiles) {

                profileNames[i] = profileA.getId().toString();


                Double maxPearson = Double.MIN_VALUE;
                Profile maxProfile = profileA;

                int j = 0;

                for (Profile profileB : profiles) {
                    if (profileA.equals(profileB)) {
                        distanceMatrix[i][j] = 0;
                        j++;
                        continue;
                    }

                    double[] vectorA = profileA.getVector();
                    double[] vectorB = profileB.getVector();

                    PearsonsCorrelation pearson = new PearsonsCorrelation();
                    Double pearsonCorrelation = pearson.correlation(vectorA, vectorB);

                    if (pearsonCorrelation >= maxPearson) {
                        maxPearson = pearsonCorrelation;
                        maxProfile = profileB;
                    }

                    double[] profileAasDouble = UtilsTransform.intArrayToDouble(profileA.getColors());
                    double[] profileBasDouble = UtilsTransform.intArrayToDouble(profileB.getColors());

                    Double pearsonOfColors = pearson.correlation(profileAasDouble, profileBasDouble);
                    distanceMatrix[i][j] = pearsonOfColors;
                    j++;
                }

                profileA.setCorrelatedVector(maxProfile.getListWrapper());

                SortedSet<StringDouble> positivePeptides = UtilsStatistics.influentialPeptides(
                        profileA.getVector(), maxProfile.getVector(), referenceProfile, true);

                profileA.setPositivePeptides(UtilsTransform.SortedSetToHTML(positivePeptides, false));

                DecimalFormat df = new DecimalFormat("0.0000");
                String peptideCorrelation = " <br/><br/><b style=\"color: #23527c;\">%s</b>";

                profileA.setPositiveCorrelation(maxProfile.toString() + String.format(peptideCorrelation, df.format(maxPearson)));

                profileRepository.save(profileA);
                i++;
            }

            ArrayList<String> clustered = runHierarchicalClustering(profileNames, distanceMatrix);

            for (Profile profile : profiles) {
                profile.setClusteringOrder(clustered.indexOf(profile.getId().toString()));
                profileRepository.save(profile);

            }
        }
    }

    private ArrayList<String> runHierarchicalClustering(String[] names, double[][] distances) {

        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
        Cluster cluster = alg.performClustering(distances, names,
                new AverageLinkageStrategy());
        ArrayList<String> sorted = new ArrayList<>();
        getLeaves(cluster, sorted);
        return sorted;
    }

    private void getLeaves(Cluster cluster, ArrayList<String> sorted) {
        if (cluster.isLeaf()) {
            sorted.add(cluster.getName());
        } else {
            for (Cluster cluster1 : cluster.getChildren()) {
                getLeaves(cluster1, sorted);
            }
        }
    }


    public List<String> getReferenceProfile(AssayType assayType) {
        if (assayType.equals(AssayType.GCP)) {
            return referenceGCPProfile;
        } else if (assayType.equals(AssayType.P100)) {
            return referenceP100Profile;
        } else {
            log.warn("Unknown assay type in getReferenceProfile");
        }
        return null;
    }

    public List<String> getReferenceGeneNames(AssayType assayType) {
        if (assayType.equals(AssayType.GCP)) {
            return referenceGCPGeneNames;
        } else if (assayType.equals(AssayType.P100)) {
            return referenceP100GeneNames;
        } else {
            log.warn("Unknown assay type in getReferenceGeneName");
        }
        return null;
    }
}
