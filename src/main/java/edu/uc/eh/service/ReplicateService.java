package edu.uc.eh.service;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.IdNameValue;
import edu.uc.eh.domain.ReplicateAnnotation;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chojnasm on 8/23/15.
 */

@Service
public class ReplicateService {

    private static final Logger log = LoggerFactory.getLogger(ReplicateService.class);

    private final ReplicateAnnotationRepository replicateAnnotationRepository;

    @Autowired
    public ReplicateService(ReplicateAnnotationRepository replicateAnnotationRepository) {
        this.replicateAnnotationRepository = replicateAnnotationRepository;
    }

    public void parseAndSaveReplicateAnnotations(List<IdNameValue> replicates, AssayType assayType, String jsonUrl) {



        ReplicateAnnotation replicateAnnotation = new ReplicateAnnotation();
        Integer previousId = null;
        Integer currentId;
        Integer updatedLsmIds = 0;

        int counter = 0;

        for (IdNameValue triple : replicates) {



            currentId = Integer.parseInt(triple.getId());

            if (counter == 0) previousId = currentId;

            boolean lastRow = counter == replicates.size() - 1;


            if ((!currentId.equals(previousId)) || lastRow) {

                if (lastRow) {
                    addAnnotationToReplicate(replicateAnnotation, triple, assayType);
                }

                ReplicateAnnotation replicateFromDb = replicateAnnotationRepository
                        .findFirstByReplicateId(replicateAnnotation.getReplicateId());

                if (replicateFromDb != null) {

                    if (!replicateFromDb.equals(replicateAnnotation)) {

/*
Update only if LSM ID in DB is Null but other fields are same.
 */
                        if (replicateFromDb.equalsWithoutLsmId(replicateAnnotation)
                                && replicateFromDb.getLsmId() == null) {
                            replicateAnnotationRepository.save(replicateAnnotation);
                            updatedLsmIds++;
//                            log.warn("Updated LsmId for replicate Id: "+ replicateAnnotation.getId()
//                                    + "Triple from JSON: " + triple.toString()
//                                    + "JSON url" + jsonUrl);
                        } else {
                            log.warn("+++ Two replicates with same key (db, new)");
                            log.warn("Triple from JSON: " + triple.toString());
                            log.warn("JSON url: " + jsonUrl);
                            log.warn("{}", replicateFromDb);
                            log.warn("{}", replicateAnnotation);
                            log.warn("");
                        }
                    }
                } else {
                    if (replicateAnnotation.getReplicateId() == null) {
                        log.warn("Replicate without Id: {}", currentId);
                    }
                    replicateAnnotationRepository.save(replicateAnnotation);
                }
                replicateAnnotation = null;
            }

            if (!lastRow) {
                if (replicateAnnotation == null) {
                    replicateAnnotation = new ReplicateAnnotation();
                }
                addAnnotationToReplicate(replicateAnnotation, triple, assayType);

                previousId = currentId;
                counter++;
            }
        }

        log.info("Updated LSM IDs: " + updatedLsmIds);

    }

    private void addAnnotationToReplicate(ReplicateAnnotation replicateAnnotation, IdNameValue triple, AssayType assayType) {


        String annotationName;
        String annotationValue;
        annotationName = triple.getName();
        annotationValue = triple.getValue();

        switch (annotationName) {

            case "id":
                replicateAnnotation.setReplicateId(annotationValue);
                break;
            case "cell_id":
                replicateAnnotation.setCellId(annotationValue);
                break;
            case "det_plate":
                replicateAnnotation.setDetPlate(annotationValue);
                break;
            case "det_well":
                replicateAnnotation.setDetWell(annotationValue);
                break;
            case "isomeric_smiles":
                replicateAnnotation.setIsomericSmiles(annotationValue);
                break;
            case "pert_dose":
                replicateAnnotation.setPertDose(annotationValue);
                break;
            case "pert_dose_unit":
                replicateAnnotation.setPertDoseUnit(annotationValue);
                break;
            case "pert_id":
                replicateAnnotation.setPertId(annotationValue);
                break;
            case "pert_iname":
                String firstUpper = annotationValue.substring(0,1).toUpperCase()+annotationValue.substring(1,annotationValue.length());
                replicateAnnotation.setPertiname(firstUpper);
                break;
            case "pert_time":
                replicateAnnotation.setPertTime(annotationValue);
                break;
            case "pert_time_unit":
                replicateAnnotation.setPertTimeUnit(annotationValue);
                break;
            case "pert_type":
                replicateAnnotation.setPertType(annotationValue);
                break;
            case "pert_vehicle":
                replicateAnnotation.setPertVehicle(annotationValue);
                break;
            case "pubchem_cid":
                replicateAnnotation.setPubchemCid(annotationValue);
                break;
            case "lsm_id":
                replicateAnnotation.setLsmId(annotationValue);
                break;
            case "canonical_smiles":
            case "cell_reprogrammed":
            case "det_normalization_group_vector":
            case "det_filename":
            case "pert_batch_internal_compound_enumerator":
            case "pert_batch_internal_replicate":
            case "provenance_code":
            case "pert_desc":
                // Skip these annotations
                break;
            default:
                log.warn("New replicate annotation: {}", annotationName);
        }
    }
}
