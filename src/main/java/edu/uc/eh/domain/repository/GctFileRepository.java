package edu.uc.eh.domain.repository;

import edu.uc.eh.domain.AssayType;
import edu.uc.eh.domain.GctFile;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;
import java.util.List;

/**
 * Created by chojnasm on 7/14/15.
 */
public interface GctFileRepository extends JpaRepository<GctFile,Long> {
    Collection<GctFile> findByDownloadUrl(String url);
    List<GctFile> findAll();
}
