package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.AssayType;
import edu.uc.eh.domain.PeakArea;
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

    Page<PeakArea> findAll(Pageable pageable);
    long count();

}
