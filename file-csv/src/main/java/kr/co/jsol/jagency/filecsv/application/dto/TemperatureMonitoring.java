package kr.co.jsol.jagency.filecsv.application.dto;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
public class TemperatureMonitoring {
    private UUID companyId;

//    private String apiKey; // 2024-09-10 현재 사용 X, 추후 j-erp에 apikey 기능이 도입된다면 사용한다.

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime measuredAt;

    private Integer infraredTemperature;

    private Integer maxTemperature;

    private Integer distance;

    private Integer close;

    public TemperatureMonitoring(List<String> line) {
        String proximity = line.get(3);
        switch (proximity) {
            case "비근접":
                close = 0;
                break;
            case "근접":
                close = 1;
                break;
            case "멀어짐":
                close = 2;
                break;
            // 그 외는 데이터가 비정상적이므로 처리하지 않는다.
            default:
                close = -1;
                return;
        }

        String measuredAtStr = line.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        measuredAt = LocalDateTime.parse(measuredAtStr, formatter);
        infraredTemperature = Integer.parseInt(line.get(1));
        distance = Integer.parseInt(line.get(2));
        //확인된 데이터 : 비근접, 근접, 멀어짐, 데이터 수신 실패


        maxTemperature = Integer.parseInt(line.get(4));
    }
}
