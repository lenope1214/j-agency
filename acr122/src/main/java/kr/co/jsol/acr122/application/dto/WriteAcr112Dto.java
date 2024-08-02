package kr.co.jsol.acr122.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ACR112 카드 쓰기 요청")
public class WriteAcr112Dto {
//    @NotNull(message = "섹터 ID는 필수입니다.")
//    @Pattern(regexp = "^[0-9]{1,2}$", message = "섹터 ID는 0~15까지 사용 가능합니다.")
//    @Min(value = 0, message = "섹터 ID는 0 이상이어야 합니다.")
//    @Max(value = 15, message = "섹터 ID는 15 이하여야 합니다.")
//    @Schema(description = "섹터 ID, 0~15까지 사용 가능하다.", example = "1", required = true)
//    private Integer sectorId;

    @NotBlank(message = "블록 ID는 필수입니다.")
    @Pattern(regexp = "^[0-9]{1,2}$", message = "블록 ID는 4~63까지 사용 가능합니다.")
    @Min(value = 0, message = "블록 ID는 4 이상이어야 합니다.")
    @Max(value = 15, message = "블록 ID는 63 이하여야 합니다.")
    @Schema(description = "블록 ID, 0~63이지만 1번째 섹터는 사용하지 않는 것이 좋다. 그러므로 4~63까지 사용 가능.", example = "5", required = true)
    private Integer blockId;

    @NotBlank(message = "KEY 값은 필수입니다.")
    @Pattern(regexp = "^[0-9A-Fa-f]{12}$", message = "KEY 값은 12자리의 16진수여야 합니다.")
    @Schema(description = "KEY 값", example = "FFFFFFFFFFFF", required = true)
    private String key;

    @Size(min=0, max=160, message = "데이터는 0~160자리여야 합니다.")
    @Schema(description = "데이터, 아무 값이나 입력해도 되나 16진수로 변환된 값이 너무 크면 입력이 불가능하다.")
    private String data;

    // 사용자 입력을 받는게 아닌 서버에서 계산하는 값이므로 hidden 처리
    // 0~3 = 0
    // 4~7 = 1
    @JsonIgnore
    @Schema(hidden = true)
    private Integer sectorId = Math.floorDiv(blockId, 4);
}
