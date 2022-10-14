package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
     * @param downloadPath jclient 다운로드 요청 api url
     * @return 성공 여부
     * @throws Exception 모든 에러에 대한 throw
     */
    public boolean downloadJclient(String downloadPath, boolean isReboot) throws Exception {
        URL url = null;

        // 파일 다운로드 여부 확인용
        boolean isDownloaded = false;
        try {

            String storeUrl = "jsolution/jclient";
            String fileName = "new_jclient.jar";
            // 최종적으로 C:/Jsolution/jclient.jar

            // 사이에 폴더가 없으면 에러가 발생하므로 폴더 확인
            storeUrl = storage.pathValidation(storeUrl.split("/"));

            // 폴더 + 파일명으로 저장할 위치 파일 지정
            storeUrl = storeUrl + "/" + fileName;

            //파일 다운로드할 URI 입력
            String fileUrl = target + "/api/v3/system/download/jclient";


            // 특정 url을 지정했다면, 그것으로 지정한다.
            if (downloadPath != null) {
                fileUrl = downloadPath;
            }

            // http 프로토콜 설정이 없으면 기본으로 http 붙여줌
            fileUrl = extApi.containHttpProtocol(fileUrl);


            url = new URL(fileUrl);
            // 만약 프로토콜이 https 라면 https SSL을 무시하는 로직을 수행해주어야 한다.('https 인증서 무시' 라는 키워드로 구글에 검색하면 많이 나옵니다.)

            log.info("jclient request url : {}", url);
            log.info("jclient store path : {}", storeUrl);
            File resFile = fileDownOnHttp(url, storeUrl);

            isDownloaded = resFile.exists();
            // 파일이 정상적으로 다운이 됐다면
            //  1.
            if (isReboot) cmd.reboot(); // 즉시 재부팅 명령어
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return isDownloaded;
    }

    /**
     * @param url
     * @param fileName
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    /**
     * url과 fileName을 받는다.
     * http url에서 파일을 가져온다.
     * fileName으로 해당 파일을 저장한다.
     * <p>
     * 이미 있으면 덮어씌운다고 한다..
     *
     * @param url      파일을 받을 http 경로
     * @param filePath 저장할때 사용할 파일 경로 + 명 example = c://jsolution/jclient/jclient.jar
     * @return 가져온 File
     * @throws MalformedURLException 파일이 없을때 오류,
     * @throws IOException           데이터 처리 작업 중 오류,
     */
    public File fileDownOnHttp(String url, String filePath) throws MalformedURLException, IOException {
        return fileDownOnHttp(new URL(url), filePath);
    }

    public File fileDownOnHttp(URL url, String filePath) throws MalformedURLException, IOException {
        File f = new File(filePath);
        FileUtils.copyURLToFile(url, f);
        return f;
    }
}
