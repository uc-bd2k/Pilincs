package edu.uc.eh.domain.repository;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.domain.PeptideAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;


/**
 * Created by chojnasm on 7/17/15.
 */
public interface PeptideAnnotationRepository extends JpaRepository<PeptideAnnotation,Long> {
    Collection<PeptideAnnotation> findByPeptideId(String peptide);
    PeptideAnnotation findFirstByPeptideId(String peptide);

    List<PeptideAnnotation> findByAssayType(AssayType assayType);


}
