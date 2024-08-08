package kr.co.jsol.jagency.mifare.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;

@Schema(description = "ACR122 카드 쓰기 요청")
public class WriteMifareDto {
    @Size(min = 0, max = 160, message = "데이터는 0~160자리여야 합니다.")
    @Schema(description = "데이터, 아무 값이나 입력해도 되나 16진수로 변환된 값이 너무 크면 입력이 불가능하다.")
    private String data;

    public @Size(min = 0, max = 160, message = "데이터는 0~160자리여야 합니다.") String getData() {
        return data;
    }

    public void setData(@Size(min = 0, max = 160, message = "데이터는 0~160자리여야 합니다.") String data) {
        this.data = data;
    }
}
