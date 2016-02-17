package edu.uc.eh.utils;

import edu.uc.eh.domain.repository.GctFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by chojnasm on 7/14/15.
 * This class is used to maintain a connectivity to MySQL database.
 */

@Component
public class ScheduleUpdate {

    private static final Logger log = LoggerFactory.getLogger(ScheduleUpdate.class);
    private final GctFileRepository gctFileRepository;

    @Autowired
    public ScheduleUpdate(GctFileRepository gctFileRepository) {
        this.gctFileRepository = gctFileRepository;
    }

    @Scheduled(fixedRate = 3600000)//once an hour
    public void run() {
        //Its a dirty hack to maintain DB connectivity.
        gctFileRepository.count();
    }
}
