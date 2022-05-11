package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Component
public class Storage {

    public String pathValidation(String... paths) throws IOException {
        String filePath = "c:/";
        log.info("paths.length : {}" ,paths.length);
        log.info(filePath);
        File dirFile = new File(filePath);
        // 루트 폴더가 존재하는지 확인 및 없으면 생성.
        log.info("파일이 있나요? : {}", dirFile.exists());
        if (!dirFile.exists()) Files.createDirectory(dirFile.toPath());

        // 하위 경로로 들어갈때, 해당 폴더가 없으면 오류가 발생함.
        // 이를 해결하기 위해 폴더 체크 및 생성
        for (int i = 0; i < paths.length; i++) {
            filePath = filePath + "/" + paths[i];
            log.info("for filePath : {}", filePath);
            dirFile = new File(filePath);
            if (!dirFile.exists()) Files.createDirectory(dirFile.toPath());
        }
        // 전체 경로를 반환한다.
        return filePath;
    }

}
