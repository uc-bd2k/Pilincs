package edu.uc.eh.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by chojnasm on 7/14/15.
 */

@EnableScheduling
public class ScheduleUpdate {

    @Scheduled(cron = "* 15 03 * * MON-FRI")
    void run() {
//TODO

    }
}
