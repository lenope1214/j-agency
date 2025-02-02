package kr.co.jsol.jagency.file.applicaiton;

import kr.co.jsol.jagency.common.infrastructure.exception.entity.FileException;
import kr.co.jsol.jagency.file.infrastructure.dto.FileDto;
import kr.co.jsol.jagency.file.infrastructure.dto.LocalFileSteamingInfoDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class FileService {
    protected final String filePathPrefix;

    public FileService(String filePathPrefix) {
        this.filePathPrefix = filePathPrefix;
    }

    public FileDto getValidatedFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
        }
        String fileOriginalName = multipartFile.getOriginalFilename();
        if (fileOriginalName == null || !fileOriginalName.contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 확장자가 없습니다.");
        }
        if (multipartFile.getSize() > 1024L * 1024 * 100) {
            // 파일 사이즈가 100mb 이상이면 업로드 불가
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 사이즈가 너무 큽니다. 최대 100MB 까지 업로드 가능합니다.");
        }

        String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf(".") + 1);
        String filename = UUID.randomUUID().toString() + "." + fileExtension;
        String downloadUrl = filePathPrefix + File.separatorChar + filename;

        return new FileDto(
                fileOriginalName,
                filename,
                fileExtension,
                downloadUrl,
                multipartFile.getSize()
        );
    }

    /**
     * 메서드 구현시 무조건 getValidatedFile(MultipartFile) 메서드로 받은 검증된 파일 사용
     *
     * @param multipartFile
     */
    public abstract FileDto addFile(MultipartFile multipartFile);

    public abstract Resource getFile(String filename);

    public abstract boolean deleteFiles(List<String> filenames);

    public abstract boolean deleteFile(String filename);

    public abstract long getDirectorySize(String companyId);

    public abstract Integer flushUnusedFiles(List<String> filenames);

    public abstract LocalFileSteamingInfoDto streaming(String filename, HttpHeaders headers);

    public abstract FileDto unzip(Resource resource, FileDto resourceFIleDto);

    public void isVideoFile(@NotNull MultipartFile file) {
        if (!Objects.requireNonNull(file.getContentType()).contains("video")) {
            throw new FileException.NotVideoFormatException();
        }
    }

    public void isImageFile(@NotNull MultipartFile file) {
        if (!Objects.requireNonNull(file.getContentType()).contains("image")) {
            throw new FileException.NotImageFormatException();
        }
    }
}
