package edu.uc.eh.domain.repository;

import edu.uc.eh.datatypes.AssayType;
import edu.uc.eh.domain.GctFile;
import edu.uc.eh.domain.PeakArea;
import edu.uc.eh.domain.ReplicateAnnotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by chojnasm on 7/14/15.
 */
public interface PeakAreaRepository extends JpaRepository<PeakArea,Long> {

    Page<PeakArea> findByReplicateAnnotationPertinameIn(Collection<String> pertInames,Pageable pageable);
    long countByReplicateAnnotationPertinameIn(Collection<String> pertInames);

    Page<PeakArea> findByGctFileAssayTypeInAndReplicateAnnotationPertinameInAndReplicateAnnotationCellIdInAndPeptideAnnotationPrGeneSymbolIn(
            List<AssayType> assayTypes,
            List<String> pertinames,
            List<String> cells,
            List<String> geneSymbols,
            Pageable pageable);


    Page<PeakArea> findAll(Pageable pageable);
    long count();

    Page<PeakArea> findByGctFileAssayTypeIn(List<AssayType> assayTypes, Pageable pageable);

    List<PeakArea> findByReplicateAnnotationId(Long replicateId);

    List<PeakArea> findByGctFileAndReplicateAnnotation(GctFile gctFile, ReplicateAnnotation replicateAnnotation);

}
