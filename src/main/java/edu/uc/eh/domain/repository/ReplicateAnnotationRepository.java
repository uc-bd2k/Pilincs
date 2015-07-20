package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.ReplicateAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


/**
 * Created by chojnasm on 7/17/15.
 */
public interface ReplicateAnnotationRepository extends JpaRepository<ReplicateAnnotation,Long>{
    Collection<ReplicateAnnotation> findByReplicateId(String replicate);
}
