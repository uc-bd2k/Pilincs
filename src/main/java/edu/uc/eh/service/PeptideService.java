package edu.uc.eh.service;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.datatypes.IdNameValue;
import edu.uc.eh.domain.PeptideAnnotation;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chojnasm on 8/23/15.
 */

@Service
public class PeptideService {

    private static final Logger log = LoggerFactory.getLogger(PeptideService.class);
    private final PeptideAnnotationRepository peptideAnnotationRepository;

    @Autowired
    public PeptideService(PeptideAnnotationRepository peptideAnnotationRepository) {
        this.peptideAnnotationRepository = peptideAnnotationRepository;
    }

    /**
     * Keep in DB only unique peptideIds and inform about unexpected situtations. Also group peptide annotations by peptideId.
     * @param peptides
     * @param assayType
     */
    public void parseAndSavePeptideAnnotations(List<IdNameValue> peptides, AssayType assayType) {

        PeptideAnnotation peptideAnnotation = new PeptideAnnotation(assayType);

        Integer previousId = null;
        Integer currentId;

        int counter = 0;

        for (IdNameValue triple : peptides) {

            currentId = Integer.parseInt(triple.getId());

            if (counter == 0) previousId = currentId;

            boolean lastRow = counter == peptides.size() - 1;


            if ((!currentId.equals(previousId)) || lastRow) {

                if (lastRow) {
                    addAnnotationToPeptide(peptideAnnotation, triple, assayType);
                }

                PeptideAnnotation peptideFromDb = peptideAnnotationRepository
                        .findFirstByPeptideId(peptideAnnotation.getPeptideId());

                if (peptideFromDb != null) {
                    if (!peptideFromDb.equals(peptideAnnotation)) {
                        log.warn("Two different peptides with the same key: {}, {}", peptideAnnotation, peptideFromDb);
                    }
                } else {
                    if (peptideAnnotation.getPeptideId() == null) {
                        log.warn("Peptide without Id: {}", currentId);
                    }
                    peptideAnnotationRepository.save(peptideAnnotation);
                }
                peptideAnnotation = null;
            }

            if (!lastRow) {
                if (peptideAnnotation == null) {
                    peptideAnnotation = new PeptideAnnotation(assayType);
                }
                addAnnotationToPeptide(peptideAnnotation, triple, assayType);

                previousId = currentId;
                counter++;
            }
        }

    }

    /**
     * Skip some annotations and merge some other annotations
     * @param peptideAnnotation
     * @param triple
     * @param assayType
     */
    private void addAnnotationToPeptide(PeptideAnnotation peptideAnnotation, IdNameValue triple, AssayType assayType) {


        String annotationName;
        String annotationValue;
        annotationName = triple.getName();
        annotationValue = triple.getValue();

        switch (annotationName) {
            case "pr_id":
                peptideAnnotation.setPeptideId(annotationValue);
                break;
            case "pr_gene_id":
                peptideAnnotation.setPrGeneId(annotationValue);
                break;
            case "pr_gene_symbol":
                peptideAnnotation.setPrGeneSymbol(annotationValue);
                break;
            case "pr_p100_base_peptide":
            case "pr_gcp_base_peptide":
                peptideAnnotation.setPrBasePeptide(annotationValue);
                break;
            case "pr_p100_cluster":
                peptideAnnotation.setPrCluster(annotationValue);
                break;
            case "pr_p100_gene_cluster_code":
                peptideAnnotation.setPrGeneClusterCode(annotationValue);
                break;
            case "pr_p100_modified_peptide_code":
            case "pr_gcp_modified_peptide_code":
                peptideAnnotation.setPrModifiedPeptideCode(annotationValue);
                break;
            case "pr_p100_original_probe_id":
                peptideAnnotation.setPrOriginalProbeId(annotationValue);
                break;
            case "pr_p100_phosphosite":
                peptideAnnotation.setPrPhosphosite(annotationValue);
                break;
            case "pr_uniprot_id":
                peptideAnnotation.setPrUniprotId(annotationValue);
                break;
            case "pr_gcp_BI_number":
                peptideAnnotation.setPrBiNumber(annotationValue);
                break;
            case "pr_gcp_histone_mark":
                peptideAnnotation.setPrHistoneMark(annotationValue);
                break;
            case "EntrezGeneId":
            case "GeneName":
            case "P100_BasePeptide":
            case "P100_Cluster":
            case "P100_GeneClusterCode":
            case "P100_ModifiedPeptideCode":
            case "P100_OriginalGeneSiteCode":
            case "pr_P100_original_gene_site_code":
            case "P100_OriginalProbeID":
            case "PhosphoSite":
            case "UniprotAC":
            case "pr_probe_normalization_group":
            case "pr_probe_suitability_manual":
                break;
            default:
                log.warn("New peptide annotation {}", annotationName);
        }
    }
}
