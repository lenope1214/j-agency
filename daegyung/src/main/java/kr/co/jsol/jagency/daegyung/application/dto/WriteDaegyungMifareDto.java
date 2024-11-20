package kr.co.jsol.jagency.daegyung.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

@Schema(description = "[대경] ACR122 태그 쓰기 요청")
public class WriteDaegyungMifareDto {
    @Size(min = 0, max = 160, message = "데이터는 0~160자리여야 합니다.")
    @Schema(description = "태그넘버, 너무 큰 값을 입력하면 에러가 발생한다")
    private String tagNo;

    public @Size(min = 0, max = 160, message = "태그넘버는 0~160자리여야 합니다.") String getData() {
        return tagNo;
    }

    public void setData(@Size(min = 0, max = 160, message = "태그넘버는 0~160자리여야 합니다.") String tagNo) {
        this.tagNo = tagNo;
    }
}
