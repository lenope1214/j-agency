package kr.co.jsol.jagency.common.application;

import kr.co.jsol.jagency.common.application.cmd.CmdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VersionChecker {

    private final Logger log = LoggerFactory.getLogger(VersionChecker.class);

    @Value("${app.version:}")
    private String appVersion;

    private final CmdService cmdService;
    private final VersionManagerService versionManagerService;


    public VersionChecker(CmdService cmdService, VersionManagerService versionManagerService) {
        this.cmdService = cmdService;
        this.versionManagerService = versionManagerService;
    }

    @PostConstruct
    public void init() {
        log.info("ls -l 실행으로 cmd 테스트");
        String result = cmdService.runNGetCmd("ls -l");
        log.info("ls -l 결과 : {}", result);
        try {
            // 버전을 가져온다.
            String version = versionManagerService.getVersion();
            log.info("현재 앱 버전 : {}", appVersion);
            log.info("최신 반영 버전 : {}", version);

            if (!version.equals(appVersion)) {
                log.info("최신 반영 버전을 다운로드 합니다.");
                boolean downloaded = versionManagerService.download(false);
                log.info("파일 다운로드 결과 : {}", downloaded);

                // 다운로드 성공시 재시작 파일을 실행하여 재시작
                if (downloaded) {
                    cmdService.restartProgram();
                }
            }

//            versionManagerService.download(false);
        } catch (Exception e) {
            log.error("app download error : {}", e.getMessage());
        }
    }

}
