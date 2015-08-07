package edu.uc.eh.utils;

import edu.uc.eh.datatypes.StringDouble;
import edu.uc.eh.domain.*;
import edu.uc.eh.domain.repository.*;

import edu.uc.eh.datatypes.AssayType;
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

    private final ConnectPanorama connectPanorama;
    private final ParseGCT parser;
    private final GctFileRepository gctFileRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final ProfileRepository profileRepository;

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(ConnectPanorama connectPanorama,
                          ParseGCT parser,
                          GctFileRepository gctFileRepository,
                          PeakAreaRepository peakAreaRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository,
                          ProfileRepository profileRepository) {
        this.connectPanorama = connectPanorama;
        this.parser = parser;
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.profileRepository = profileRepository;
    }

    @PostConstruct
    private void initDatabase() throws IOException, CommandException {
        loadRawData();
        loadProfiles();
    }

    private void loadProfiles() {
        log.info("Filling repository with profiles");
        for(ReplicateAnnotation replicateAnnotation : replicateAnnotationRepository.findAll()){

            AssayType assayType;
            Long replicateId = replicateAnnotation.getId();
            List<PeakArea> peaksForReplicate = peakAreaRepository.findByReplicateAnnotationId(replicateId);
            List<StringDouble> profileVector = new ArrayList<>();

            for(PeakArea peakArea : peaksForReplicate){

                profileVector.add(new StringDouble(peakArea.getPeptideAnnotation().getPeptideId(),
                        peakArea.getValue() == null ? 0.0 : peakArea.getValue()));
            }

            assayType = peaksForReplicate.get(0).getGctFile().getAssayType();

            Profile profile = new Profile(replicateAnnotation, assayType, profileVector);

            profileRepository.save(profile);
        }

        HashMap<Integer,ArrayList<Profile>> profilesByLength = new HashMap<>();
        for(Profile profile : profileRepository.findAll()){

            Integer length = profile.getVector().size();
            if (!profilesByLength.containsKey(length)) {
                profilesByLength.put(length, new ArrayList<Profile>());
            }
            profilesByLength.get(length).add(profile);
        }

        DecimalFormat df = new DecimalFormat("0.0000");
        Iterator it = profilesByLength.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Profile> profiles = (ArrayList<Profile>) pair.getValue();
            for(Profile profileA : profiles){
                double[] doublesA = profileA.getVectorDoubles();
                Double maxPearson = -1.0;
                Double minPearson = 1.0;

                Profile maxProfile = profileA;
                Profile minProfile = profileA;

                for(Profile profileB : profiles){
                    if(profileA.equals(profileB))continue;

                    double[] doublesB = profileB.getVectorDoubles();

                    PearsonsCorrelation pearson = new PearsonsCorrelation();

                    Double pearsonCorrelation = pearson.correlation(doublesA,doublesB);
                    if(pearsonCorrelation >= maxPearson){
                        maxPearson = pearsonCorrelation;
                        maxProfile = profileB;
                    }
                    if(pearsonCorrelation <= minPearson){
                        minPearson = pearsonCorrelation;
                        minProfile = profileB;
                    }
                }

                SortedSet<StringDouble> positivePeptides = CalculateUtils.influentialPeptides(profileA, maxProfile, true);
                SortedSet<StringDouble> negativePeptides = CalculateUtils.influentialPeptides(profileA, minProfile, false);


                profileA.setPositivePeptides(TransformUtils.SortedSetToHTML(positivePeptides,false));
                profileA.setNegativePeptides(TransformUtils.SortedSetToHTML(negativePeptides,true));

                profileA.setPositiveCorrelation(maxProfile.toString() + " <br/><br/><b style=\"color: #23527c;\">" + df.format(maxPearson) + "</b>" );
                profileA.setNegativeCorrelation(minProfile.toString() + " <br/><br/><b style=\"color: #23527c;\">" + df.format(minPearson) + "</b>" );

                profileRepository.save(profileA);
            }
        }


    }

    private void loadRawData() throws IOException, CommandException {
        log.info("Loading raw data from panorama etc");
        List<String> list = connectPanorama.GctUrls();
        int counter = 0;
        for(String url : list){
//            if(counter++ > 0) continue;
            HashMap<String, List<ParseGCT.AnnotationValue>> metaProbes = new HashMap<>();
            HashMap<String, List<ParseGCT.AnnotationValue>> metaReplicas = new HashMap<>();
            ArrayList<ParseGCT.ProbeReplicatePeak> peakValues = new ArrayList<>();

            parser.parseToRepository(url, peakValues, metaProbes, metaReplicas);

//            Cache sourceUrls
            List<String> probeIds = new ArrayList<>(metaProbes.keySet());
            HashMap<String,Integer> dbPeptideIds = connectPanorama.getPeptideIds(probeIds,
                    ParseUtils.parseArrayTypeFromUrl(url),
                    ParseUtils.parseRunId(url)
                    );
//
            if(dbPeptideIds.size()!=probeIds.size()){
                log.warn("STOP, stop, stop!!!");
            }

            GctFile gctfile = new GctFile(url);
            gctfile.setRunId(ParseUtils.parseRunId(url));
            gctfile.setRunIdUrl(connectPanorama.getRunIdLink(gctfile));
            gctFileRepository.save(gctfile);


            for(ParseGCT.ProbeReplicatePeak peak : peakValues) {

                String probeId = peak.getProbeId();
                String replicateId = peak.getReplicateId();

                PeptideAnnotation peptideAnnotation = peptideAnnotationRepository.findFirstByPeptideId(probeId);
                ReplicateAnnotation replicateAnnotation = replicateAnnotationRepository.findFirstByReplicateId(replicateId);

                if(peptideAnnotation == null){
                    peptideAnnotation = new PeptideAnnotation(probeId);
                    for(ParseGCT.AnnotationValue annotationObject : metaProbes.get(probeId)){
                        String annotationName = annotationObject.getAnnotationName();
                        String annotationValue = annotationObject.getAnnotationValue();

                        switch(annotationName){
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
                            default:
//                                log.warn("New annotation: {}",annotationName);
                        }
                    }
                }

                if(replicateAnnotation == null){
                    replicateAnnotation = new ReplicateAnnotation(replicateId);
                    for(ParseGCT.AnnotationValue annotationObject : metaReplicas.get(replicateId)){
                        String annotationName = annotationObject.getAnnotationName();
                        String annotationValue = annotationObject.getAnnotationValue();

                        switch(annotationName){
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
                }

                peptideAnnotationRepository.save(peptideAnnotation);
                replicateAnnotationRepository.save(replicateAnnotation);

                PeakArea peakArea = new PeakArea(gctfile,peptideAnnotation,replicateAnnotation,peak.getPeakArea());
                peakArea.setSourceUrl(connectPanorama.getSourceUrlFast(
                        ParseUtils.parseArrayTypeFromUrl(url),
                        dbPeptideIds.get(peakArea.getPeptideAnnotation().getPeptideId()),
                        peakArea.getReplicateAnnotation().getReplicateId()));

                peakAreaRepository.save(peakArea);


            }
        }
    }

}
