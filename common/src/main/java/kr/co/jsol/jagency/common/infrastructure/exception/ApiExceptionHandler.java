package kr.co.jsol.jagency.common.infrastructure.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.invoke.MissingParametersException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionBody> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        if (message == null) {
            message = "요청 매개변수가 잘못되었습니다. 매개변수를 확인해주세요.";
        }
        log.error("handleMethodArgumentNotValidException - message : {}", message);
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    // @Valid 검증 실패 시 Catch
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ExceptionBody> handleInvalidParameterException(InvalidParameterException ex) {
        log.error("handleInvalidParameterException - message : {}", ex.getMessage());
        return new GeneralClientException.BadRequestException().toEntity(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionBody> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("dataIntegrityViolationException - message : {}", ex.getMessage());
        String message = "데이터 제약조건 오류가 발생했습니다. " + ex.getMessage();
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ExceptionBody> invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
        log.error("invalidDataAccessApiUsageException - message : {}", ex.getMessage());
        return new GeneralClientException.BadRequestException().toEntity(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionBody> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        ex.printStackTrace();
        log.error("illegalArgumentExceptionHandler - message : {}", ex.getMessage());
        return new GeneralServerException.InternalServerException().toEntity(ex.getMessage());
    }

    /**
     * 요청 매개변수가 잘못되었을 경우
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> dtoTypeMissMatchException(HttpMessageNotReadableException ex) {
        String msg;
        Throwable causeException = ex.getCause();
        if (causeException instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) causeException;
            msg = "입력 받은 " + ife.getValue() + " 를 " + ife.getTargetType() + " 으로 변환중 에러가 발생했습니다.";
        } else if (causeException instanceof MissingParametersException) {
            MissingParametersException mkpe = (MissingParametersException) causeException;
            msg = "Parameter is missing: " + mkpe.getMessage();
        } else {
            msg = "요청을 역직렬화 하는과정에서 예외가 발생했습니다.";
        }
        return new GeneralClientException.BadRequestException().toEntity(msg);
    }

    /**
     * 요청 매개변수가 잘못되었을 경우
     * ex) String을 String? 타입처럼 null을 보냈을 경우 BindingException 발생
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionBody> beanPropertyBindingResult(BindException ex) {
        String codes = ex.getBindingResult().getFieldErrors().stream()
                .map(it -> {
                    log.error("filedError : {}, {}", it.getField(), it.getDefaultMessage());
                    return it.getField() + " : " + it.getDefaultMessage();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        String message = "요청 매개변수가 잘못되었습니다. 매개변수를 확인해주세요. 상세 정보 : " + codes;
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    /**
     * 토큰 바디가 존재 하지 않을 경우
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ExceptionBody> servletRequestBindingException(ServletRequestBindingException ex) {
        log.error("ServletRequestBindingException - message : {}", ex.getMessage());
        String message = "요청 매개변수가 잘못되었습니다. 매개변수를 확인해주세요.";
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    /**
     * ValidationException
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<ExceptionBody> unexpectedTypeException(UnexpectedTypeException ex) {
        log.error("UnexpectedTypeException - message : {}", ex.getMessage());
        return new GeneralServerException.InternalServerException().toEntity(ex.getMessage());
    }

    /**
     * NullPointException 예외 처리 메서드입니다.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionBody> handleNullPointException(NullPointerException ex) {
        ex.printStackTrace();
        log.error("NullPointerException - message : {}", ex.getMessage());
        return new GeneralServerException.ServerNullPointerException().toEntity();
    }

    /**
     * 사용자 권한 부족으로 인한 접근 권한 거부
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ExceptionBody> springSecurityAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        log.error("Spring Security - AccessDeniedException - message : {}", ex.getMessage());
        return new GeneralClientException.ForbiddenException().toEntity(ex.getMessage());
    }

    /**
     * 실 존재하는 파일 접근 권한 부족으로 인한 접근 권한 거부
     */
    @ExceptionHandler(java.nio.file.AccessDeniedException.class)
    public ResponseEntity<ExceptionBody> fileAccessDeniedException(java.nio.file.AccessDeniedException ex) {
        log.error("FileSystemException -  AccessDeniedException - message : {}", ex.getMessage());
        String message = "서버에서 요청한 파일을 찾아올 수 없었습니다. 담당 개발자에게 연락바랍니다.";
        return new GeneralServerException.InternalServerException().toEntity(message);
    }

    /**
     * 요청 핸들러에서 해당하는 요청 메서드가 없을 경우 발생하는 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionBody> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException - message : {}", ex.getMessage());
        String message = ex.getLocalizedMessage() + " 요청 메서드를 확인해 주세요";
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    /**
     * NoSuchElementException 예외 처리 메서드입니다.
     *
     * @param ex 예외 객체
     * @return ResponseEntity<ExceptionBody>
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionBody> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("NoSuchElementException - localizedMessage: {}", ex.getLocalizedMessage());
        String message = "요청이 올바르지 않습니다. \n" + ex.getLocalizedMessage();
        return new GeneralClientException.BadRequestException().toEntity(message);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionBody> handleCustomException(CustomException ex) {
        log.error("CustomException - message : {}", ex.getMessage());
        return ex.toEntity();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> handleException(Exception ex) {
        log.error("Exception - message : {}", ex.getLocalizedMessage());
        log.error(ex.getMessage(), ex);
        return new CustomException("000", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR).toEntity();
    }
}
