package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.ReplicateAnnotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;;


/**
 * Created by chojnasm on 7/17/15.
 */
public interface ReplicateAnnotationRepository extends JpaRepository<ReplicateAnnotation,Long>{
    Collection<ReplicateAnnotation> findByReplicateId(String replicate);
    ReplicateAnnotation findFirstByReplicateId(String replicate);

    List<ReplicateAnnotation> findAll();
    Page<ReplicateAnnotation> findAll(Pageable pageable);
}
