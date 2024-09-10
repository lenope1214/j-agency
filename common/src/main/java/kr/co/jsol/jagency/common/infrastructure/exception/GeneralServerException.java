package kr.co.jsol.jagency.common.infrastructure.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public abstract class GeneralServerException {

    public static class InternalServerException extends CustomException {
        public InternalServerException() {
            this("서버 에러 발생, 담당 개발자에게 연락해주세요.");
        }

        public InternalServerException(String message) {
            this(message, null);
        }

        public InternalServerException(String message, Throwable e) {
            super("GNR-5000", message != null ? message : "서버 에러 발생, 담당 개발자에게 연락해주세요.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public static class ServerNullPointerException extends CustomException {
        public ServerNullPointerException(String message, Throwable e) {
            super("GNR-5001", message != null ? message : "서버 에러 발생, 담당 개발자에게 연락해주세요.", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        public ServerNullPointerException() {
            this("서버 에러 발생, 담당 개발자에게 연락해주세요.", null);
        }
    }

    /**
     * 파일 시스템 관리 중 에러 발생
     */
    public static class ManageSystemFileException extends CustomException {
        public ManageSystemFileException(@NonNull String message, Throwable e) {
            super("GNR-FILE-0001", message, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        public ManageSystemFileException(@NonNull String message) {
            super("GNR-FILE-0001", message, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

        public ManageSystemFileException() {
            this("시스템 파일 관리 중 오류가 발생했습니다");
        }
    }
}
