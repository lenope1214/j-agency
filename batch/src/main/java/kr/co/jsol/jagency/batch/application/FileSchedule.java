package kr.co.jsol.jagency.batch.application;

import kr.co.jsol.jagency.filecsv.application.CsvToDBService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class FileSchedule {
    private final Logger log = getLogger(FileSchedule.class);
    private final CsvToDBService csvToDBService;


    public FileSchedule(CsvToDBService csvToDBService) {
        this.csvToDBService = csvToDBService;
    }

    @Value("${file-to-db.active:false}")
    private Boolean active;

    @Scheduled(fixedDelay = 10000)
    public void run() {
        if (!active) {
            log.info("file-to-db.active is false. Exit the program.");
            System.exit(0);
        }

        log.info("file-to-db.active is true. Start the program.");
        LocalDateTime lastUploadTime = csvToDBService.lastUploadTime();
        log.info("Last upload time: {}", lastUploadTime);
    }
}
