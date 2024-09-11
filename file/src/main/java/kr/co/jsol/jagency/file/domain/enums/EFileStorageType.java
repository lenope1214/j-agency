// EFileStorageType.java
package kr.co.jsol.jagency.file.domain.enums;

import lombok.Getter;

@Getter
public enum EFileStorageType {
    LOCAL("서버"),
    DB("DB"),
    MINIO("Minio");

    private final String description;

    EFileStorageType(String description) {
        this.description = description;
    }

}
