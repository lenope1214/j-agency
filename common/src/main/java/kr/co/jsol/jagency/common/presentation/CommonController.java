package kr.co.jsol.jagency.common.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "999. 공통 API")
@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {
    @Operation(summary = "J-Agency 통신 상태 체크 API")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "성공")})
    @GetMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean checkNetworkStatus() {
        return true;
    }
}
