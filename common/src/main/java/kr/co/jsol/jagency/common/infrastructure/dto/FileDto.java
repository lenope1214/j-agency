package kr.co.jsol.jagency.common.infrastructure.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Schema(name = "파일 정보")
public class FileDto {
    @Schema(description = "원본 파일명")
    private String originalName;

    @Schema(description = "저장된 파일명, 기본은 random UUID")
    private String name;

    @Schema(description = "확장자")
    private String extension;

    @Schema(description = "다운로드 URL")
    private String downloadUrl;

    @Schema(description = "파일 크기")
    private Long size;

    public FileDto(String originalName, String name, String extension, String downloadUrl, long size) {
        this.originalName = originalName;
        this.name = name;
        this.extension = extension;
        this.downloadUrl = downloadUrl;
        this.size = size;
    }
}
