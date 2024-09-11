package kr.co.jsol.jagency.schedules;

import kr.co.jsol.jagency.common.application.StorageService;
import kr.co.jsol.jagency.filecsv.application.CsvToDBService;
import kr.co.jsol.jagency.filecsv.application.dto.JerpCreateTemperatureMonitoringRequest;
import kr.co.jsol.jagency.filecsv.application.dto.TemperatureMonitoring;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class DaedongFileBackupSchedule {
    private final Logger log = getLogger(DaedongFileBackupSchedule.class);
    private final RestTemplate restTemplate;
    private final CsvToDBService csvToDBService;
    private final StorageService storageService;
    private final String VERSION_FILENAME = "version.txt";

    public DaedongFileBackupSchedule(
            RestTemplate restTemplate,
            CsvToDBService csvToDBService,
            StorageService storageService
    ) {
        this.restTemplate = restTemplate;
        this.csvToDBService = csvToDBService;
        this.storageService = storageService;
    }

    @Value("${file-to-db.active:false}")
    private Boolean active;

    @Value("${file-to-db.key:}")
    private String key;

    @Value("${file-to-db.company-id:}")
    private String companyId;

    @Value("${file-to-db.file-path:}")
    private String filePath;

    @Value("${file-to-db.api-url:}")
    private String apiUrl;

    @PostConstruct
    private void init() {
        if (key == null || key.isEmpty()) {
            log.error("file-to-db.key is empty. Exit the program.");
            System.exit(0);
        }

        if (companyId == null || companyId.isEmpty()) {
            log.error("file-to-db.company-id is empty. Exit the program.");
            System.exit(0);
        }

        if (filePath == null || filePath.isEmpty()) {
            log.error("file-to-db.file-path is empty. Exit the program.");
            System.exit(0);
        }

        if (apiUrl == null || apiUrl.isEmpty()) {
            log.error("file-to-db.api-url is empty. Exit the program.");
            System.exit(0);
        }
    }

    // 매일 1시 0분 0초에 실행
    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        // active가 false거나, key가 "daedong"이 아니면 실행하지 않는다.
        if (!active || !key.equals("daedong")) {
            return;
        }

        log.info("file-to-db.active is true. Start the program.");
        LocalDateTime lastUploadTime = getLastUploadTime();
        log.info("Last upload time: {}", lastUploadTime);

        // 마지막 업데이트 시간 이후로 파일을 읽어온다.
        LocalDate _date = lastUploadTime.toLocalDate();
        LocalDate tomorrow = LocalDate.now();
        // 오늘자 데이터는 아직 업데이트 중일 것이므로 전날까지의 데이터만 백업한다.
        while (_date.isBefore(tomorrow)) {
            String csvFilePath = filePath + File.separatorChar + _date.toString() + ".csv";
            List<List<String>> readData = csvToDBService.readData(csvFilePath);

            if (readData == null || readData.isEmpty()) {
                log.error("Failed to read data from file: {}", csvFilePath);
                _date = _date.plusDays(1);
                continue;
            }

//            csvToDBService.printData(readData);
            // 마지막 data 가져와서 측정일시 확인
            List<String> last = readData.get(readData.size() - 1);
            LocalDateTime lastMeasuredAt = new TemperatureMonitoring(last).getMeasuredAt();

            List<TemperatureMonitoring> temperatureMonitorings = new ArrayList<>();

            readData.forEach(line -> {
                TemperatureMonitoring requestDto = new TemperatureMonitoring(line);

                // -1은 비정상 데이터로 분류
                if (requestDto.getClose() == -1) {
                    return;
                }

                temperatureMonitorings.add(requestDto);
            }); // end of readData.forEach

            // 데이터 앞 100개 확인
//            for (int i = 0; i < 100; i++) {
//                log.info("{}", requestBody.get(i));
//            }

            JerpCreateTemperatureMonitoringRequest requestBody = new JerpCreateTemperatureMonitoringRequest(companyId, temperatureMonitorings);

            // https://j-erp-production.huclo.co.kr/api/v1/temperature-monitoring/j-agency/backup
            ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(apiUrl, requestBody, Boolean.class);
            Boolean body = responseEntity.getBody();
            if (body == null || !body) {
                // 실패, 데이터 백업
                log.error("Failed to backup data!!");
//                log.error("Failed to backup data: {}", requestBody);
            }

            // 성공, 해당 파일을 삭제한다.
            File file = new File(csvFilePath);
            if (file.delete()) {
                log.info("File deleted successfully: {}", csvFilePath);
            } else {
                log.error("Failed to delete file: {}", csvFilePath);
            }

            // 마지막 측정일시 업데이트
            writeLastUploadTime(lastMeasuredAt);
            _date = _date.plusDays(1);
        }

    }


    private LocalDateTime getLastUploadTime() {
        // 파일을 읽어서 마지막 업로드 시간 확인
        // 파일이 없다면 새로 생성하고, 파일에 현재 시간을 입력한다.
        LocalDateTime lastUploadTime = null;
        try {
            String fullPath = filePath + File.separatorChar + VERSION_FILENAME;
            log.debug("version.txt 파일 경로 : {}", fullPath);
            storageService.pathGenerate(filePath);
            FileReader in = null;
            try {
                in = new FileReader(fullPath);
                log.debug("version.txt 파일을 읽어옵니다.");
            } catch (FileNotFoundException e) {
                log.error("version.txt 파일이 없습니다. 새로 생성합니다.");
                BufferedWriter bw = new BufferedWriter(new FileWriter(fullPath));
                bw.write(LocalDateTime.now().toString());
                bw.flush();
                bw.close();
                in = new FileReader(fullPath);
                log.debug("version.txt 파일을 생성했습니다.");
            }

            BufferedReader br = new BufferedReader(in);
            String line = br.readLine();

            // 파일이 비어있으면 현재 시간을 기록
            if (line == null) {
                lastUploadTime = LocalDateTime.now();
                br.close();
                writeLastUploadTime(lastUploadTime);
            }
            // 파일이 비어있지 않으면 파일에 기록된 시간을 가져옴
            else {
                lastUploadTime = LocalDateTime.parse(line);
                br.close();
            }

            br.close();
        } catch (IOException e) {
            log.error("version.txt 파일을 읽어오는데 실패했습니다.");
            e.printStackTrace();
        }
        return lastUploadTime;
    }

    private void writeLastUploadTime(LocalDateTime lastUploadTime) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(VERSION_FILENAME));
            bw.write(lastUploadTime.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            log.error("version.txt 파일을 쓰는데 실패했습니다.");
            e.printStackTrace();
        }
    }
}
