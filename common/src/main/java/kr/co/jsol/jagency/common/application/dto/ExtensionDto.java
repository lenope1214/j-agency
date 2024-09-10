package kr.co.jsol.jagency.common.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.jsol.jagency.common.infrastructure.annotation.JDateTimeFormat;
import kr.co.jsol.jagency.common.infrastructure.dto.FileDto;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Schema(name = "확장프로그램 정보")
public class ExtensionDto {
    private String id;

    @JDateTimeFormat
    private LocalDateTime createdAt;

    @JDateTimeFormat
    private LocalDateTime updatedAt;

    private String version;

    private String folder;

    private FileDto file;
}
