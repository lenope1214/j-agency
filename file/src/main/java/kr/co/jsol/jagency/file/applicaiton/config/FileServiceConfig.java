package kr.co.jsol.jagency.file.applicaiton.config;

import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import kr.co.jsol.jagency.file.applicaiton.FileService;
import kr.co.jsol.jagency.file.applicaiton.LocalFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfig {

    @Value("${file.mode:local}")
    private String fileMode;

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.target}")
    private String fileTarget;

    private static final Logger log = LoggerFactory.getLogger(FileServiceConfig.class);

    @Bean
    public FileService fileService() {
        log.info("fileMode: {}", fileMode);

        if (fileMode.equals("local")) {
            log.info("fileDir: {}", fileDir);
            log.info("fileTarget: {}", fileTarget);

            // only local mode
            assert fileDir == null || fileDir.isEmpty() : "Could not resolve placeholder 'file.dir' in value \"${file.dir}\"";
            assert fileTarget == null || fileTarget.isEmpty() : "Could not resolve placeholder 'file.url' in value \"${file.target}\"";

            return new LocalFileService(fileDir, fileTarget);
        } else {
            throw new GeneralServerException.InternalServerException("Invalid file mode: " + fileMode);
        }

    }
}
