package kr.co.jsol.jagency.common.application.schedules;

import kr.co.jsol.jagency.common.application.VersionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailySchedule {
    private final Logger log = LoggerFactory.getLogger(DailySchedule.class);
    private final VersionChecker versionChecker;

    public DailySchedule(VersionChecker versionChecker) {
        this.versionChecker = versionChecker;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailySchedule() {
        log.info("Daily Schedule");

        // version check
        log.info("[Daily Schedule - 001] check j-agency version");
        versionChecker.init();
    }
}
