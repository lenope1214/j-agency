package kr.co.jsol.jagency.common.application;

import kr.co.jsol.jagency.common.application.cmd.CmdService;
import kr.co.jsol.jagency.common.application.dto.ExtensionDto;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
public class VersionManagerService extends RestService {
    @Value("${app.file-server-host:}")
    private String appFileServerHost;

    @Value("${app.version-url:}")
    private String appVersionUrl;

    @Value("${app.get-extension-info-url:}")
    private String appInfoUrl;

    @Value("${app.file-path:}")
    private String appFilePath;

    private final RestTemplate restTemplate;

    private final StorageService storageService;

    private final CmdService cmd;

    public VersionManagerService(RestTemplate restTemplate, StorageService storageService, CmdService cmd) {
        this.restTemplate = restTemplate;
        this.storageService = storageService;
        this.cmd = cmd;
    }

    public String getVersion() {
        String apiUrl = appFileServerHost + appVersionUrl;
        log.info("[VMS - getVersion] apiUrl : {}", apiUrl);
//
//            // http 프로토콜 설정이 없으면 기본으로 http 붙여줌
        apiUrl = containHttpProtocol(apiUrl);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);
        return responseEntity.getBody();
    }

    public boolean download(boolean isReboot) {
        try {
            storageService.pathGenerate(appFilePath);

            // 파일 정보 가져오기
            String apiUrl = appFileServerHost + appInfoUrl;
//
//            // http 프로토콜 설정이 없으면 기본으로 http 붙여줌
            apiUrl = containHttpProtocol(apiUrl);
            log.info("[VMS - download] app info api url : {}", apiUrl);

            // 파일 정보 조회
            ExtensionDto extensionDto = restTemplate.getForObject(apiUrl, ExtensionDto.class);
            if (extensionDto == null) {
                log.error("파일 정보를 가져오지 못했습니다.");
                throw new GeneralServerException.ManageSystemFileException();
            }
            String downloadUrl = extensionDto.getFile().getDownloadUrl();

            final String newFileName = "new_jagency.jar";
            File resFile = fileDownOnHttp(downloadUrl, appFilePath + File.separatorChar + newFileName);

            if (isReboot) cmd.rebootPc(); // 즉시 재부팅 명령어
            // 파일이 정상적으로 다운이 됐다면 true 반환
            return resFile.exists();
        } catch (IllegalArgumentException e) {
            log.error("파일 저장 경로가 지정되어있지 않습니다. 'app.file-path'를 확인해주세요.");
            return false;
        } catch (Exception e) {
            log.error("파일 다운로드 중 에러 발생 : {}", e.getMessage());
            e.printStackTrace();
            return false;
        }


//        log.info("file : {}", file);
//        return file.exists();

//        URL url = null;
//
//        // 파일 다운로드 여부 확인용
//        boolean isDownloaded = false;
//        try {
//
//            String storeUrl = "C://jsolution/jagency";
//            String fileName = "new_jagency.jar";
//            // 최종적으로 C:/Jsolution/jclient.jar
//
//            // 사이에 폴더가 없으면 에러가 발생하므로 폴더 확인
//            storeUrl = storageService.pathGenerate(storeUrl.split("/"));
//
//            // 폴더 + 파일명으로 저장할 위치 파일 지정
//            storeUrl = storeUrl + "/" + fileName;
//
//            //파일 다운로드할 URI 입력
//            String fileUrl = appDownloadUrl;
//
//            // http 프로토콜 설정이 없으면 기본으로 http 붙여줌
//            fileUrl = containHttpProtocol(fileUrl);
//
//
//            url = new URL(fileUrl);
//            // 만약 프로토콜이 https 라면 https SSL을 무시하는 로직을 수행해주어야 한다.('https 인증서 무시' 라는 키워드로 구글에 검색하면 많이 나옵니다.)
//
//            log.info("jclient request url : {}", url);
//            log.info("jclient store path : {}", storeUrl);
//            File resFile = fileDownOnHttp(url, storeUrl);
//
//            isDownloaded = resFile.exists();
//            // 파일이 정상적으로 다운이 됐다면
//            if (isReboot) cmd.reboot(); // 즉시 재부팅 명령어
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//        return isDownloaded;
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
