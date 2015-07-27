package edu.uc.eh.service;

import edu.uc.eh.domain.*;
import edu.uc.eh.domain.repository.GctFileRepository;
import edu.uc.eh.domain.repository.PeakAreaRepository;

import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import org.labkey.remoteapi.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    private static final Logger log = LoggerFactory.getLogger(DatabaseLoader.class);

    @Autowired
    public DatabaseLoader(ConnectPanorama connectPanorama,
                          ParseGCT parser,
                          GctFileRepository gctFileRepository,
                          PeakAreaRepository peakAreaRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository) {
        this.connectPanorama = connectPanorama;
        this.parser = parser;
        this.gctFileRepository = gctFileRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
    }

    @PostConstruct
    private void initDatabase() throws IOException, CommandException {
        List<String> list = connectPanorama.GctUrls();
        int counter = 0;
        for(String url : list){
            //if(counter++ > 0) continue;
            HashMap<String, List<ParseGCT.AnnotationValue>> metaProbes = new HashMap<>();
            HashMap<String, List<ParseGCT.AnnotationValue>> metaReplicas = new HashMap<>();
            ArrayList<ParseGCT.ProbeReplicatePeak> peakValues = new ArrayList<>();

            parser.parseToRepository(url, peakValues, metaProbes, metaReplicas);

//            Cache sourceUrls
            List<String> probeIds = new ArrayList<>(metaProbes.keySet());
            HashMap<String,Integer> dbPeptideIds = connectPanorama.getPeptideIds(probeIds,
                    Utils.parseArrayTypeFromUrl(url),
                    Utils.parseRunId(url)
                    );
//
            if(dbPeptideIds.size()!=probeIds.size()){
                log.warn("STOP, stop, stop!!!");
            }

            GctFile gctfile = new GctFile(url);
            gctfile.setRunId(Utils.parseRunId(url));
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
                        Utils.parseArrayTypeFromUrl(url),
                        dbPeptideIds.get(peakArea.getPeptideAnnotation().getPeptideId()),
                        peakArea.getReplicateAnnotation().getReplicateId()));

                peakAreaRepository.save(peakArea);


            }
        }

//        Page<PeakArea> users = peakAreaRepository.findAll(new PageRequest(1, 20));
//        for(PeakArea pa:users){
//            System.out.println(pa);
//        }
//        System.out.println(users.getNumberOfElements());

//        for(String a : list) {
//            GctFile gctfile = new GctFile(a);
//            PeptideAnnotation pepAnn = null;
//            ReplicateAnnotation repAnn = null;
//            if (gctfile.getAssayType().equals(AssayType.GCP)) {
//                gctfile.setRunId(2144);
//
//                pepAnn = peptideAnnotationRepository.save(new PeptideAnnotation("BI10007"));
//                repAnn = replicateAnnotationRepository.save(new ReplicateAnnotation("GM7-49453-003A03"));
//            } else {
//                gctfile.setRunId(2108);
//                pepAnn = peptideAnnotationRepository.save(new PeptideAnnotation("10011_DYRK_Y321_IYQY[+80]IQSR"));
//                repAnn = replicateAnnotationRepository.save(new ReplicateAnnotation("PA9-79EE-001A01"));
//            }
//            gctfile.setRunIdUrl(connectPanorama.getRunIdLink(gctfile));
//            gctFileRepository.save(gctfile);
//
//
//            PeakArea pa1 = new PeakArea(gctfile, pepAnn, repAnn, 34.2);
//            PeakArea pa2 = new PeakArea(gctfile, pepAnn, repAnn, -15.1);
//
//            pa1.setSourceUrl(connectPanorama.getSourceUrl(pa1));
//            pa2.setSourceUrl(connectPanorama.getSourceUrl(pa2));
//
//            peakAreaRepository.save(pa1);
//            peakAreaRepository.save(pa2);
//        }
    }

}
