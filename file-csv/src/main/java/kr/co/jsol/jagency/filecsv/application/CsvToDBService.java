package kr.co.jsol.jagency.filecsv.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;

@Service
public class CsvToDBService {
    private final Logger log = LoggerFactory.getLogger(CsvToDBService.class);
    // 경로에 파일들 가져오기
    private final String versionFile = "version.txt";

    public LocalDateTime lastUploadTime() {
        // 파일을 읽어서 마지막 업로드 시간 확인
        // 파일이 없다면 새로 생성하고, 파일에 현재 시간을 입력한다.
        LocalDateTime lastUploadTime = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(versionFile));
            String line = br.readLine();

            if (line == null) {
                lastUploadTime = LocalDateTime.now();
                br.close();
                writeLastUploadTime(lastUploadTime);
            } else {
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
            BufferedWriter bw = new BufferedWriter(new FileWriter(versionFile));
            bw.write(lastUploadTime.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            log.error("version.txt 파일을 쓰는데 실패했습니다.");
            e.printStackTrace();
        }
    }


}
