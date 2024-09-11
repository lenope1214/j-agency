package kr.co.jsol.jagency.filecsv.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JerpCreateTemperatureMonitoringRequest {
    private String companyId;

    private List<TemperatureMonitoring> temperatureMonitorings;
}
