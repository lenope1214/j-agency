package kr.co.jsol.jagency.filecsv.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class CsvToDBService {
    private final Logger log = LoggerFactory.getLogger(CsvToDBService.class);

    @Value("${file-to-db.file-path:}")
    private String filePath;

    private final CsvReader csvReader;

    public CsvToDBService(CsvReader csvReader) {
        this.csvReader = csvReader;
    }

    public File getCsvFile(String filename) {
        File file = new File(filePath + File.separatorChar + filename);
        if (!file.exists()) {
            log.error("파일이 존재하지 않습니다. 파일명 : {}", filename);
            return null;
        }

        if (!file.canRead()) {
            log.error("파일을 읽을 수 없습니다. 파일명 : {}", filename);
            return null;
        }

        // csv 파일인지 확인
        if (!filename.endsWith(".csv")) {
            log.error("csv 파일이 아닙니다. 파일명 : {}", filename);
            return null;
        }

        return file;
    }

    public List<List<String>> readData(String filePath) {
        return csvReader.read(filePath);
    }

    public void printData(List<List<String>> csvList) {
        csvReader.printCsv(csvList);
    }
}
