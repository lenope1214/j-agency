package kr.co.jsol.jagency.common.infrastructure.exception.entity;

import kr.co.jsol.jagency.common.infrastructure.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class ExtensionException {
    private static final String NAME = "확장프로그램";
    private static final String CODE = "EXTENSION";

    public static class NotFoundByIdException extends CustomException {

        public NotFoundByIdException(String message, Throwable e) {
            super(CODE + "-0001", message != null ? message : "id로 " + NAME + " 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, e);
        }

        public NotFoundByIdException() {
            this("id로 " + NAME + " 정보를 찾을 수 없습니다.", null);
        }
    }
}
