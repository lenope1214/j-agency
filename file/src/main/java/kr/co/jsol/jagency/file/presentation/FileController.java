package kr.co.jsol.jagency.file.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.jsol.jagency.common.infrastructure.dto.FileDto;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;
import kr.co.jsol.jagency.file.applicaiton.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "9999. 파일", description = "파일 관리 API")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "파일'만' 업로드, 주로 업로드 테스트 용도 ")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public FileDto uploadFile(
            @RequestPart("file") MultipartFile file
    ) {
        return fileService.addFile(file);
    }

    @Operation(summary = "파일 조회")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "파일이 존재하지 않습니다.")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("{filename}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Resource> getFile(
            @PathVariable
            String filename
    ) {
        Resource resource = fileService.getFile(filename);

        MediaType contentType = MediaTypeFactory.getMediaType(resource)
                .orElseThrow(() -> new GeneralClientException.BadRequestException("파일이 존재하지 않습니다."));

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(contentType)
                .header("Accept-Ranges", "bytes")
                .body((FileSystemResource) resource);
    }

}
