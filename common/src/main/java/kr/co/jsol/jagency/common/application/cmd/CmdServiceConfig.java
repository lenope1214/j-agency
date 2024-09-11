package kr.co.jsol.jagency.common.application.cmd;

import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CmdServiceConfig {

    private static final Logger log = LoggerFactory.getLogger(CmdServiceConfig.class);

    // 크롬 배치파일이 없다면 기본으로 chrome을 실행한다.
    @Value("${batUrl:start /b C://Jsolution/jclient/waitViewer.bat}")
    private String batUrl;
    
    @Bean
    public CmdService cmdService() {
        String os = System.getProperty("os.name");
        log.info("os: {}", os);
        // Windows
        if (os.toLowerCase().contains("win")) {
            return new WindowsCommandService(batUrl);
        }
        // Mac OS
        else if (os.toLowerCase().contains("mac")) {
            return new LinuxCommandService(batUrl);
        }
        // Linux
        else if (os.toLowerCase().contains("nix") || os.toLowerCase().contains("nux") || os.toLowerCase().contains("aix")) {
            return new LinuxCommandService(batUrl);
        } else {
            throw new GeneralServerException.InternalServerException("지원하지 않는 OS입니다.");
        }
    }
}
