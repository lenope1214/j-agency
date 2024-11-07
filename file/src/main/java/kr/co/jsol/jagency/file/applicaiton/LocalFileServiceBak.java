package kr.co.jsol.jagency.file.applicaiton;

import kr.co.jsol.jagency.common.infrastructure.dto.FileDto;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import kr.co.jsol.jagency.file.domain.enums.EFileStorageType;
import kr.co.jsol.jagency.file.infrastructure.dto.LocalFileSteamingInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.Long.min;

public class LocalFileServiceBak extends FileServiceBak {
    private final String fileDir;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LocalFileServiceBak(String fileDir) {
        super(EFileStorageType.LOCAL, fileDir);
        this.fileDir = fileDir;
    }

    @Override
    public FileDto addFile(MultipartFile multipartFile) {
        FileDto file = super.getValidatedFile(multipartFile);

        checkOrInsertFolderFullPath(fileDir);
        try {
            File localFile = new File(fileDir + File.separatorChar + file.getName());
            multipartFile.transferTo(localFile);
        } catch (Exception e) {
            logger.error("{} 파일 업로드 실패 : {}", file.getOriginalName(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드를 실패했습니다.");
        }

        return file;
    }

    @Override
    public FileSystemResource getFile(String filename) {
        if (!existsFile(filename)) {
            throw new RuntimeException("파일을 찾을수 없습니다.");
        }

        return new FileSystemResource(fileDir + File.separatorChar + filename);
    }

    @Override
    public LocalFileSteamingInfoDto streaming(String filename, HttpHeaders headers) {
        String path = fileDir + File.separatorChar + filename;
        FileSystemResource resource = new FileSystemResource(path);

        long chunkSize = 1024 * 1024;
        long contentLength = 0;
        try {
            contentLength = resource.contentLength();
        } catch (IOException e) {
            logger.error("파일 사이즈를 가져오는데 실패했습니다. filename : {}", filename);
            throw new GeneralServerException.InternalServerException("파일 사이즈를 가져오는데 실패했습니다.", e);
        }

        try {
            HttpRange httpRange = headers.getRange().stream().findFirst().get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = min(chunkSize, end - start + 1);
            return new LocalFileSteamingInfoDto(
                    path,
                    resource,
                    new ResourceRegion(resource, start, rangeLength)
            );
        } catch (Exception e) {
            long rangeLength = min(chunkSize, contentLength);
            return new LocalFileSteamingInfoDto(
                    path,
                    resource,
                    new ResourceRegion(resource, 0, rangeLength)
            );
        }
    }

    @Override
    public boolean deleteFiles(List<String> filenames) {
        boolean result = true;
        for (String filename : filenames) {
            if (!deleteFile(filename)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean deleteFile(String filename) {
        String savedPath = fileDir + File.separatorChar + filename;
        logger.info("FileDeleter - filename : " + savedPath + " 를 삭제 시도 합니다.");
        File file = new File(savedPath);

        if (existsFile(filename)) {
            boolean result = file.delete();

            if (!result) {
                logger.error("파일삭제 실패! filename : {}", filename);
                return false;
            } else {
                logger.info("파일삭제 성공! filename : {}", filename);
                return true;
            }
        }
        logger.info("삭제할 파일이 없습니다. 일단 통과! filename : {}", filename);
        return true;
    }

    @Override
    public long getDirectorySize(String companyId) {
        String savedPath = fileDir + File.separatorChar + companyId;
        File file = new File(savedPath);
        return file.length();
    }

    @Override
    public void flushUnusedFiles(String companyId, List<String> files) {
        String savedPath = fileDir + File.separatorChar + companyId;
        File folder = new File(savedPath);
        File[] fileList = folder.listFiles();
        if (fileList == null) {
            logger.error("flushUnusedFiles - fileList is null");
            return;
        }
        for (File file : fileList) {
            if (!files.contains(companyId + File.separatorChar + file.getName())) {
                // return 값은 사용하지 않음
                file.delete();
            }
        }
    }

    public boolean existsFile(String filename) {
        return existsFilePath(fileDir + File.separatorChar + filename);
    }

    public boolean existsFilePath(String fullPath) {
        return new File(fullPath).isFile();
    }

    public void checkOrInsertFolderFullPath(String path) {
        try {
            String[] paths = path.split(String.valueOf(File.separatorChar));
            StringBuilder fullPath = new StringBuilder();
            for (String p : paths) {
                fullPath.append(p).append(File.separatorChar);
                createFolderIfNotExists(fullPath.toString());
            }
        } catch (Exception e) {
            logger.error("createFolderFullPath error : " + e.getMessage());
        }
    }

    public void createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
