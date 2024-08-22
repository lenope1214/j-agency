package kr.co.jsol.jagency.common.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class StorageService {

    public String pathGenerate(String filePath) throws IOException, IllegalArgumentException {
        String[] split = filePath.split("/");
        List<String> pathList = Arrays.asList(split);
        if (pathList.isEmpty()) throw new IllegalArgumentException("paths is empty");
        String nowFilePath = pathList.get(0);
        log.info("기본 루트 경로 : {}", nowFilePath);
        // 만약 pathList의 제일 앞 경로가 /로 시작해서 빈 값일 경우 그 다음 값을 루트 경로로 설정한다.
        if (nowFilePath.isEmpty()) nowFilePath = "/" + pathList.get(1);
        // 제일 앞 값 자체를 삭제해서 한 칸씩 이동
        pathList = pathList.subList(1, pathList.size());

//        log.info("paths.length : {}" ,paths.length);
//        log.info(filePath);
        log.info("적용 루트 경로 : {}", nowFilePath);
        File dirFile = new File(nowFilePath);
//        // 루트 폴더가 존재하는지 확인 및 없으면 생성.
        log.info("{} 에 파일이 있나요? : {}", nowFilePath, dirFile.exists());
        if (!dirFile.exists()) Files.createDirectory(dirFile.toPath());

        // 하위 경로로 들어갈때, 해당 폴더가 없으면 오류가 발생함.
        // 이를 해결하기 위해 폴더 체크 및 생성
        for (int i = 1; i < pathList.size(); i++) {
            nowFilePath = nowFilePath + "/" + pathList.get(i);
//            log.info("for nowFilePath : {}", nowFilePath);
            dirFile = new File(nowFilePath);
            log.info("{} 에 파일이 있나요? : {}", nowFilePath, dirFile.exists());

            if (!dirFile.exists()) Files.createDirectory(dirFile.toPath());
        }
        // 전체 경로를 반환한다.
        return nowFilePath;
    }

}
