package edu.uc.eh.service;

import edu.uc.eh.domain.json.AssayRecord;
import edu.uc.eh.domain.PeakArea;
import edu.uc.eh.domain.json.Query;
import edu.uc.eh.domain.repository.PeakAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chojnasm on 7/20/15.
 */

@Service
public class QueryService {

    PeakAreaRepository peakAreaRepository;

    @Autowired
    public QueryService(PeakAreaRepository peakAreaRepository) {
        this.peakAreaRepository = peakAreaRepository;
    }

    public List<AssayRecord> getAllAssays(List<Query> tags) {
        List<AssayRecord> output = new ArrayList<>();

        for(Query query : tags){
            int oneTagUpTo100=0;

            Collection<PeakArea> listGCP=null;// = peakAreaRepository
//                    .findByReplicateAnnotationPertInameAndGctFileAssayType(query.getText(), AssayType.GCP);
            Collection<PeakArea> listP100=null;// = peakAreaRepository
//                    .findByReplicateAnnotationPertInameAndGctFileAssayType(query.getText(), AssayType.P100);

            Iterator<PeakArea> itGCP = listGCP.iterator();
            Iterator<PeakArea> itP100 = listP100.iterator();
            boolean keepGoing = true;

            while (keepGoing && (itGCP.hasNext() || itP100.hasNext())) {

                for(int i = 0;i<5;i++) {
                    if (itGCP.hasNext()) output.add(new AssayRecord(itGCP.next()));
                }
                for(int i = 0;i<5;i++) {
                    if (itP100.hasNext()) output.add(new AssayRecord(itP100.next()));
                }
                oneTagUpTo100++;
                if(output.size() > 1000) return output;
                if(oneTagUpTo100 > 10) {
                    keepGoing = false;
                }else{
                    keepGoing = true;
                }

            }
        }
        return output;
    }
}
