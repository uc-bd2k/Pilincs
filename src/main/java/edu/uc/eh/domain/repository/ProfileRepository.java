package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.Profile;
import edu.uc.eh.datatypes.AssayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by chojnasm on 7/31/15.
 */
public interface ProfileRepository extends JpaRepository<Profile,Long> {
    List<Profile> findAll();
    Page<Profile> findByReplicateAnnotationCellIdInAndReplicateAnnotationPertinameIn(
            List<String> cells, List<String> pertiname, Pageable pageable);

    Page<Profile> findByAssayTypeInAndReplicateAnnotationCellIdInAndReplicateAnnotationPertinameIn(
            List<AssayType> assays, List<String> cells, List<String> pertiname, Pageable pageable);
}
