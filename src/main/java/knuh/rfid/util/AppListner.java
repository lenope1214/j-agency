package knuh.rfid.util;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import knuh.rfid.RFID;

@Component
@Slf4j
public class AppListner implements CommandLineRunner {

    // : 을 붙여서 기본값을 부여함.   java jclient --mode 이런 properties를 입력 안 했을때 오류 나지 않도록 설정해준다.
    @Value("${mode:default}")
    String mode;
    @Value("${ip:}")
    String ip;
    @Value("${target:}")
    String target;
    @Value("${batUrl:}")
    String batUrl;

    @Value("${ext.appVersion:}")
    private String appVersion;


    @Autowired
    private KnuhApiServiceImpl knuhApiService;

    @Autowired
    private VersionManagerService versionManagerService;

    @Autowired
    private CmdImpl cmdService;

    @Override
    public void run(String... args) throws Exception {

        log.info("mode : {}", mode);
        log.info("ip : {}", ip);
        log.info("target : {}", target);
        log.info("batUrl : {}", batUrl);
        log.info("appVersion : {}", appVersion);

        // ip가 입력 되어있다면, version 확인 후 재실행
        if(ip!=null && !ip.equals("null")){
            if(knuhApiService == null)knuhApiService = new KnuhApiServiceImpl();
            String version = knuhApiService.get("/api/v3/jclient/version");
            log.info("server - jclient - version : {}", version);
            log.info("local - jlcient - version : {}", appVersion);

            // 버전이 같으면 안 함.
            if(!version.equals(appVersion)){
                // 버전이 다르면, 최신 데이터(Jclient.jar) 받아오고
                // 업데이트 후 실행하는 배치파일 실행
                
                // 파일 업데이트 다운로드
                boolean downloadResult = versionManagerService.downloadJclient(null, false);


                if(downloadResult){
                    // 다운로드 성공 했다면,
                    // 배치 파일을 실행한다.
                    // 배치 파일 구성은
                    cmdService.runCmd("reset_jclient.bat");
                    // 1. 현재 실행중인 jclient를 죽인다.
                    // 2. 이전 버전 jclient.jar를 삭제한다.
                    // 3. 최신 버전 new_jclient.jar를 jclient.jar로 파일명을 변경한다.
                    // 4-2. 컴퓨터를 재부팅시킨다. -> shell:startup에 jclient 실행 배치가 있을 것이므로 가능할것
                }else{
                    // 다운로드에 실패 했다면,
                    // 실패 했음을 서버에 던진다.
                    HashMap<String, Object> logMap = new HashMap<>();
                    logMap.put("message", "jclient 자동 다운로드 실패, 현재 버전 : "+version);
                    knuhApiService.post("/api/v1/log/error", logMap);
                }
            }
        }

        if (mode != null && mode.equals("rfid")) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("ip", ip);
            params.put("target", target);
            RFID rfid = new RFID(params);
            Thread thread = new Thread(rfid);
            thread.start();
        } else
            System.out.println("Default Mode");
    }
}
