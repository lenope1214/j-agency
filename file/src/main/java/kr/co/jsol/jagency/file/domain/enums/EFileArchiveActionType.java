package kr.co.jsol.jagency.file.domain.enums;

public enum EFileArchiveActionType {
    UPLOAD("업로드"),
    DELETE("삭제"),
    DOWNLOAD("다운로드"),
    RENAME("이름 변경"),
    MOVE("이동"),
    COPY("복사"),
    RESTORE("복원"),
    DELETE_PERMANENTLY("영구 삭제"),
    CREATE_FOLDER("폴더 생성"),
    DELETE_FOLDER("폴더 삭제"),
    MOVE_FOLDER("폴더 이동"),
    COPY_FOLDER("폴더 복사"),
    RESTORE_FOLDER("폴더 복원"),
    DELETE_PERMANENTLY_FOLDER("폴더 영구 삭제"),
    RENAME_FOLDER("폴더 이름 변경"),
    UPLOAD_FOLDER("폴더 업로드"),
    DOWNLOAD_FOLDER("폴더 다운로드");

    private final String description;

    EFileArchiveActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
