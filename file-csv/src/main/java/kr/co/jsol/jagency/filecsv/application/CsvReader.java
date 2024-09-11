package kr.co.jsol.jagency.filecsv.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CsvReader {
    private final Logger log = LoggerFactory.getLogger(CsvReader.class);

    public List<List<String>> read(String csvFilePath) {
        List<List<String>> csvList = new ArrayList<List<String>>();
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(
                    // FileReader 사용시 한글 깨짐 현상 발생, InputStreamReader 사용하여 해결
                    new InputStreamReader(
                            new FileInputStream(csvFilePath),
                            "EUC-KR"
                    )
            );

            // 첫 row는 header로 생각하고 skip
            String header = br.readLine();
            log.info("header : {}", header);

            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
//                log.info("read line : {}", line);
                List<String> list = new ArrayList<>();
                Collections.addAll(list, token);
                csvList.add(list);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvList;
    }

    public void printCsv(List<List<String>> csvList) {
        long count = 0L;
        for (List<String> list : csvList) {
            String line = "";
            for (String str : list) {
                line += str + ",";
            }
            if (!line.isEmpty()) {
                line = line.substring(0, line.length() - 1);
//                log.info(line);
//                System.out.println("["+(count++ + 1) + "] = " + line);
            }
        }
    }

    public void writeCsv(List<List<String>> csvList, String csvFilePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath));
            for (List<String> list : csvList) {
                for (String str : list) {
                    bw.write(str);
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCsv(List<List<String>> csvList, String csvFilePath, String header) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath));
            bw.write(header);
            bw.newLine();
            for (List<String> list : csvList) {
                for (String str : list) {
                    bw.write(str);
                    bw.write(",");
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
