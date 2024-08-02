package kr.co.jsol.jagency.common.infrastructure.dto;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
public class DateRangeDto {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRangeDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
