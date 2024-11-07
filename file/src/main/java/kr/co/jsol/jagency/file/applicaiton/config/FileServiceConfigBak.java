package kr.co.jsol.jagency.file.applicaiton.config;

import kr.co.jsol.jagency.file.applicaiton.FileService;
import kr.co.jsol.jagency.file.applicaiton.LocalFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceConfigBak {

    @Value("${spring.file.mode:local}")
    private String fileMode;

    @Value("${spring.file.dir:}")
    private String fileDir;

    private static final Logger log = LoggerFactory.getLogger(FileServiceConfigBak.class);

    @Bean
    public FileService fileService() {
        log.info("fileMode: {}", fileMode);
        // only local mode
        assert fileDir != null : "Could not resolve placeholder 'file.dir' in value \"${file.dir}\"";
        return new LocalFileService(fileDir);
    }
}
