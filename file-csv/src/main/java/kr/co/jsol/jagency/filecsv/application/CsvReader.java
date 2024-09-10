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
        File csv = new File(csvFilePath);
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csv));
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
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
        for (List<String> list : csvList) {
            for (String str : list) {
                log.info(str);
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
