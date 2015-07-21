package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.AssayType;
import edu.uc.eh.domain.PeakArea;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;

/**
 * Created by chojnasm on 7/14/15.
 */
public interface PeakAreaRepository extends JpaRepository<PeakArea,Long> {

    Collection<PeakArea> findByGctFileDownloadUrl(String url);
    Collection<PeakArea> findByGctFileId(Long id);
    Collection<PeakArea> findByReplicateAnnotationPertInameAndGctFileAssayType(String pertIname, AssayType type);
}
