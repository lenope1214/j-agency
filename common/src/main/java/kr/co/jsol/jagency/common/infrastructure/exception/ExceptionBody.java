package kr.co.jsol.jagency.common.infrastructure.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "기본 에러")
public class ExceptionBody {
    @Schema(description = "에러 코드")
    private final String code;

    @Schema(description = "에러 메시지")
    private final String message;

    @Schema(description = "에러 상태 코드")
    private final int status;

    public ExceptionBody() {
        this.code = "GNR-0000";
        this.message = "알 수 없는 에러가 발생했습니다.";
        this.status = 500;
    }

    public ExceptionBody(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
