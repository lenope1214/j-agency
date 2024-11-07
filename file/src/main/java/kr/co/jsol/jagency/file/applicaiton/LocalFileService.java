package kr.co.jsol.jagency.file.applicaiton;

import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import kr.co.jsol.jagency.file.infrastructure.dto.FileDto;
import kr.co.jsol.jagency.file.infrastructure.dto.LocalFileSteamingInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.Long.min;

public class LocalFileService extends FileService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String fileDir;

    public LocalFileService(String fileDir, String fileTarget) {
        super(fileTarget + "/api/files"); // api 경로랑 맞춘다.
        this.fileDir = fileDir;
    }

    @Override
    public FileDto addFile(MultipartFile multipartFile) {
        String fileDir = this.fileDir;

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
    public Integer flushUnusedFiles(List<String> filenames) {
        Integer deletedCount = 0;
        File folder = new File(fileDir);

        if (!folder.exists() || !folder.isDirectory()) {
            logger.info("flushUnusedFiles - 삭제할 파일이 없습니다.");
            return 0;
        }

        // 재귀적으로 폴더 내의 파일을 삭제하는 메소드 호출
        deletedCount += deleteUnusedFiles(folder, filenames);

        return deletedCount;
    }

    private int deleteUnusedFiles(File folder, List<String> filenames) {
        int deletedCount = 0;
        File[] fileList = folder.listFiles();

        if (fileList == null) {
            return 0;
        }

        for (File file : fileList) {
            if (file.isDirectory()) {
                // 재귀적으로 폴더 내의 파일을 삭제
                deletedCount += deleteUnusedFiles(file, filenames);
            } else {
                if (!filenames.contains(file.getName())) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
        }

        // 폴더가 비어있는 경우 삭제 (폴더 자체를 삭제하지 않으려면 아래 조건문을 제거)
        //if (Objects.requireNonNull(folder.listFiles()).length == 0) {
        //    folder.delete();
        //}

        return deletedCount;
    }

    @Override
    public FileDto unzip(Resource resource, FileDto resourceFileDto) {
        logger.info("unzip - resource : {}", resource);
        File file;
        String zipFilePath;
        String fileFullName;
        String filename;
        String fileExtension;
        String destDir;

        try {
            file = resource.getFile();
            zipFilePath = file.getAbsolutePath();
            fileFullName = file.getName();
            filename = fileFullName.substring(0, fileFullName.lastIndexOf("."));
            fileExtension = fileFullName.substring(fileFullName.lastIndexOf(".") + 1);
            destDir = file.getParent() + File.separatorChar + filename;
        } catch (IOException e) {
            logger.error("unzip - resource.getFile().getAbsolutePath() error : {}", e.getMessage());
            throw new GeneralServerException.InternalServerException("압축파일을 찾을 수 없습니다. 다시 요청해주세요.");
        }

        logger.info("unzip - zipFilePath : {}, destDir : {}, fileFullName : {}, filename : {}, fileExtension : {}",
                zipFilePath, destDir, fileFullName, filename, fileExtension);

        if (!zipFilePath.endsWith(".zip")) {
            logger.error("unzip - zipFilePath is not zip file : {}", zipFilePath);
            throw new GeneralClientException.BadRequestException("zip 파일이 아닙니다.");
        }

        String baseDir = file.getParent();
        String extractDir = baseDir + File.separatorChar + filename;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            byte[] buffer = new byte[1024];
            while (zipEntry != null) {
                String entryName = zipEntry.getName();

                // ZIP 파일 내 최상위 디렉토리 제거
                logger.info("unzip - entryName : {}", entryName);
                logger.info("unzip - entryName.startsWith(filename + \"/\") : {}", entryName.startsWith(filename + "/"));
                logger.info("unzip - resourceFileDto.getOriginalName().startsWith(entryName) : {}", resourceFileDto.getOriginalName().startsWith(entryName));
                if (entryName.startsWith(filename + "/") || resourceFileDto.getOriginalName().startsWith(entryName)) {
                    entryName = entryName.substring(filename.length() + 1);
                }

                if (!entryName.isEmpty()) {
                    File newFile = new File(extractDir, entryName);
                    logger.info("unzip - processing : {}", newFile.getAbsolutePath());

                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("디렉토리를 생성할 수 없습니다: " + newFile);
                        }
                    } else {
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("디렉토리를 생성할 수 없습니다: " + parent);
                        }

                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            logger.error("압축파일을 압축해제하는데 실패했습니다.", e);
            throw new GeneralServerException.InternalServerException("압축파일을 압축해제하는데 실패했습니다.", e);
        }
//        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
//            ZipEntry zipEntry = zis.getNextEntry();
//            byte[] buffer = new byte[1024];
//            while (zipEntry != null) {
//                String entryName = zipEntry.getName();
//
//                // 전체 경로를 사용하여 파일 생성
//                File newFile = new File(destDir, entryName);
//                logger.info("unzip - processing : {}", newFile.getAbsolutePath());
//
//                if (zipEntry.isDirectory()) {
//                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
//                        throw new IOException("디렉토리를 생성할 수 없습니다: " + newFile);
//                    }
//                } else {
//                    // 부모 디렉토리 생성
//                    File parent = newFile.getParentFile();
//                    if (!parent.isDirectory() && !parent.mkdirs()) {
//                        throw new IOException("디렉토리를 생성할 수 없습니다: " + parent);
//                    }
//
//                    // 파일 쓰기
//                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
//                        int len;
//                        while ((len = zis.read(buffer)) > 0) {
//                            fos.write(buffer, 0, len);
//                        }
//                    }
//                }
//                zipEntry = zis.getNextEntry();
//            }
//        } catch (IOException e) {
//            logger.error("압축파일을 압축해제하는데 실패했습니다.", e);
//            throw new GeneralServerException.InternalServerException("압축파일을 압축해제하는데 실패했습니다.", e);
//        }

        if (!deleteFile(fileFullName)) {
            logger.error("unzip - deleteFile error : {}", fileFullName);
            // 압축 파일은 삭제해도 실패로 처리하지 않고 batch로 처리한다.
        }

        return new FileDto(
                resourceFileDto.getOriginalName(),
                filename,
                fileExtension,
                filePathPrefix + File.separatorChar + filename,
                resourceFileDto.getSize()
        );
    }

    public boolean existsFile(String filename) {
        if (filename == null) {
            return false;
        }
        if (filename.startsWith(File.separator)) {
            filename = filename.substring(1);
        }
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
