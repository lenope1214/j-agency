package kr.co.jsol.jagency.file.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum EFileType {
    IMAGE("이미지", Arrays.asList("png", "jpg", "jpeg", "gif")),
    VIDEO("동영상", Arrays.asList("mp4")),
    FILE("일반 파일", Arrays.asList("pdf", "docx", "xlsx", "txt", "xls", "ppt", "pptx", "pps", "ppsx", "hwp", "hwpx", "tif", "xml", "iml")),
    ;

    private final String description;
    private final List<String> validFileExtensions;

    EFileType(String description, List<String> validFileExtensions) {
        this.description = description;
        this.validFileExtensions = validFileExtensions;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getValidFileExtensions() {
        return validFileExtensions;
    }

    public static EFileType fromExtension(String extension) {
        for (EFileType fileType : EFileType.values()) {
            if (fileType.getValidFileExtensions().contains(extension)) {
                return fileType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다.");
    }
}
