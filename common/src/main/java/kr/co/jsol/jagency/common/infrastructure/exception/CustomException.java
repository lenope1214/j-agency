package kr.co.jsol.jagency.common.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class CustomException extends RuntimeException {
    private final String code;
    private final HttpStatus status;
    private Throwable throwable = null;

    public CustomException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public CustomException(String code, String message, HttpStatus status, Throwable throwable) {
        super(message);
        this.code = code;
        this.status = status;
        this.throwable = throwable;
    }

    public ResponseEntity<ExceptionBody> toEntity(String overrideMessage) {
        return ResponseEntity.status(status)
                .body(
                        new ExceptionBody(
                                code,
                                overrideMessage != null ? overrideMessage : getMessage(),
                                status.value()
                        )
                );
    }

    public ResponseEntity<ExceptionBody> toEntity() {
        return toEntity(null);
    }
}
