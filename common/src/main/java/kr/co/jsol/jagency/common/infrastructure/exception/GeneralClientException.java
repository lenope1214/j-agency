package kr.co.jsol.jagency.common.infrastructure.exception;

import org.springframework.http.HttpStatus;

public abstract class GeneralClientException {

    public static class BadRequestException extends CustomException {
        public BadRequestException() {
            this(null, null);
        }

        public BadRequestException(String message) {
            this(message != null ? message : "잘못된 요청입니다. 데이터를 확인해주세요.", null);
        }

        public BadRequestException(String message, Throwable e) {
            super("GNR-4000", message != null ? message : "잘못된 요청입니다. 데이터를 확인해주세요.", HttpStatus.BAD_REQUEST, e);
        }
    }

    public static class UnauthorizedException extends CustomException {
        public UnauthorizedException(String message, Throwable e) {
            super("GNR-4010", message != null ? message : "인증이 필요합니다.", HttpStatus.UNAUTHORIZED, e);
        }

        public UnauthorizedException() {
            this("인증이 필요합니다.", null);
        }
    }

    public static class InvalidTokenException extends CustomException {
        private static final String MESSAGE = "유효하지 않은 토큰입니다.";

        public InvalidTokenException(Throwable e) {
            this(MESSAGE, e);
        }

        public InvalidTokenException(String message, Throwable e) {
            super("GNR-4011", message != null ? message : MESSAGE, HttpStatus.UNAUTHORIZED, e);
        }

        public InvalidTokenException() {
            this(MESSAGE, null);
        }
    }

    public static class ForbiddenException extends CustomException {
        public ForbiddenException(String message, Throwable e) {
            super("GNR-4030", message != null ? message : "권한이 없습니다.", HttpStatus.FORBIDDEN, e);
        }

        public ForbiddenException() {
            this("권한이 없습니다.", null);
        }
    }

    public static class NotFoundException extends CustomException {
        public NotFoundException(String message, Throwable e) {
            super("GNR-4040", message != null ? message : "요청에 대한 결과를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, e);
        }

        public NotFoundException() {
            this("요청에 대한 결과를 찾을 수 없습니다.", null);
        }
    }

    public static class LoginFailedException extends CustomException {
        public LoginFailedException(String message, Throwable e) {
            super("GNR-4031", message != null ? message : "로그인 실패", HttpStatus.FORBIDDEN, e);
        }

        public LoginFailedException() {
            this("로그인 실패", null);
        }
    }

    public static class DisabledUserException extends CustomException {
        public DisabledUserException(String message, Throwable e) {
            super("GNR-4033", message != null ? message : "비활성화된 사용자입니다.", HttpStatus.FORBIDDEN, e);
        }

        public DisabledUserException() {
            this("비활성화된 사용자입니다.", null);
        }
    }


    public static class ConflictException extends CustomException {
        public ConflictException(String message, Throwable e) {
            super("GNR-4090", message != null ? message : "중복된 데이터가 존재합니다.", HttpStatus.CONFLICT, e);
        }

        public ConflictException() {
            this("중복된 데이터가 존재합니다.", null);
        }
    }
}
