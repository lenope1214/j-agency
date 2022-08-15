package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
@Slf4j

public class VersionManagerService {

    @Value("${target:}")
    String target;

    @Autowired
    private Storage storage;

    @Autowired
    private ExtApi extApi;

    @Autowired
    private CmdImpl cmd;

    /**
     *
     * @param downloadPath jclient 다운로드 요청 api url
     * @return 성공 여부
     * @throws Exception 모든 에러에 대한 throw
     */
    public boolean downloadJclient(String downloadPath, boolean isReboot) throws Exception {
        URL url = null;
        InputStream in = null;
        FileOutputStream fileOutputStream = null;
        try {

            String storeUrl = "jclient";
            String fileName = "new_jclient.jar";
            // 최종적으로 C:/Jsolution/jclient.jar

            // 사이에 폴더가 없으면 에러가 발생하므로 폴더 확인
            storeUrl = storage.pathValidation(storeUrl.split("/"));

            // 폴더 + 파일명으로 저장할 위치 파일 지정
            File file = new File(storeUrl + "/" + fileName);
            fileOutputStream = new FileOutputStream(file, false); // true 시 기존 파일이 남아있음, false 시 덮어씌움

            //파일이 존재하는 위치의 URL
//                String fileUrl = target+"/api/v1/files/shop/11/test/plzme.txt/download";
//            String fileUrl = target + "/files/download/jclient";
            String fileUrl = target + "/api/v1/jclient";


            // 특정 url을 지정했다면, 그것으로 지정한다.
            if (downloadPath != null) {
                fileUrl = downloadPath;
            }

            // http 프로토콜 설정이 없으면 기본으로 http 붙여줌
            fileUrl = extApi.containHttpProtocol(fileUrl);


            url = new URL(fileUrl);
            // 만약 프로토콜이 https 라면 https SSL을 무시하는 로직을 수행해주어야 한다.('https 인증서 무시' 라는 키워드로 구글에 검색하면 많이 나옵니다.)

            in = url.openStream();

            log.info("파일 다운 시작!");
            while (true) {
                //파일을 읽어온다.
                int data = in.read();
                if (data == -1) {
                    log.info("파일 다운 종료!");
                    break;
                }
                //파일을 쓴다.
                fileOutputStream.write(data);
            }

            in.close();
            fileOutputStream.close();
            // 파일이 정상적으로 다운이 됐다면 재부팅.
            if(isReboot)cmd.runCmd("shutdown -r -t 0"); // 즉시 재부팅 명령어
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (in != null) in.close();
            if (fileOutputStream != null) fileOutputStream.close();
            return true;
        }
    }
}
