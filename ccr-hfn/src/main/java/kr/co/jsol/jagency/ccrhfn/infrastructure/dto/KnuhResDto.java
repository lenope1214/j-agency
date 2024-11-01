package kr.co.jsol.jagency.ccrhfn.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnuhResDto {
    private boolean success;
    private Integer code;
    private String msg;
    private String data;
}
