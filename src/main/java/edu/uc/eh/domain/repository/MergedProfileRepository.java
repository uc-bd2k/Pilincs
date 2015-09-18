package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.MergedProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by chojnasm on 9/15/15.
 */
public interface MergedProfileRepository extends JpaRepository<MergedProfile, Long> {

    Page<MergedProfile> findByReplicateAnnotationCellIdInAndReplicateAnnotationPertinameIn(
            List<String> cells, List<String> pertiname, Pageable pageable);

}
