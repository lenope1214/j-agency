package kr.co.jsol.jagency.common.infrastructure.exception.entity;

import kr.co.jsol.jagency.common.infrastructure.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class FileException {
    private static final String NAME = "파일";
    private static final String CODE = "FILE";

    public static class NotFoundByIdException extends CustomException {

        public NotFoundByIdException(String message, Throwable e) {
            super(CODE + "-0001", message != null ? message : "id로 " + NAME + " 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, e);
        }

        public NotFoundByIdException() {
            this("id로 " + NAME + " 정보를 찾을 수 없습니다.", null);
        }
    }

    public static class NotVideoFormatException extends CustomException {

        public NotVideoFormatException(String message, Throwable e) {
            super(CODE + "-0005", message != null ? message : "비디오 형식이 아닙니다.", HttpStatus.BAD_REQUEST, e);
        }

        public NotVideoFormatException() {
            this("비디오 형식이 아닙니다.", null);
        }
    }

    public static class NotImageFormatException extends CustomException {

        public NotImageFormatException(String message, Throwable e) {
            super(CODE + "-0006", message != null ? message : "이미지 형식이 아닙니다.", HttpStatus.BAD_REQUEST, e);
        }

        public NotImageFormatException() {
            this("이미지 형식이 아닙니다.", null);
        }
    }

    public static class UnAllowedFileExtensionException extends CustomException {

        public UnAllowedFileExtensionException(String message, Throwable e) {
            super(CODE + "-0007", message != null ? message : "허용되지 않는 파일 확장자입니다.", HttpStatus.BAD_REQUEST, e);
        }

        public UnAllowedFileExtensionException() {
            this("허용되지 않는 파일 확장자입니다.", null);
        }
    }
}
