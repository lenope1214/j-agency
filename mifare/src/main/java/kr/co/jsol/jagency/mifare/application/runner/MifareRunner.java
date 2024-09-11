package kr.co.jsol.jagency.mifare.application.runner;

import kr.co.jsol.jagency.reader.application.runner.TagRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MifareRunner implements TagRunner {

    private final Logger log = LoggerFactory.getLogger(MifareRunner.class);

    // false시 RFID 사용X
    @Value("${mifare.use:false}")
    String use;

    private final MifareReader mifareReader;

    public MifareRunner(MifareReader mifareReader) {
        this.mifareReader = mifareReader;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("acr122 use : {}", use);

        if (use.equals("true")) {
            Thread thread = new Thread(mifareReader);
            thread.start();
        }
//
//        log.info("ip : {}", ip);
//        log.info("target : {}", target);
//        log.info("batUrl : {}", batUrl);
//        log.info("appVersion : {}", appVersion);
//
//        // ip가 입력 되어있다면, version 확인 후 재실행
//        if (ip != null && !ip.equals("null")) {
////            if (knuhApiService == null) knuhApiService = new KnuhApiServiceImpl();
//            Gson gson = new Gson();
//
//            String version = knuhApiService.get("/api/v3/system/extension/version/jclient");
//            KnuhResType extension = gson.fromJson(version, KnuhResType.class);
//            if (extension == null) {
////                throw new IllegalStateException("jclient 버전 확인에 실패했습니다.");
//                log.error("jclient 버전 확인에 실패했습니다.");
////                Thread.sleep(5000);
////                System.exit(0);
//                extension = new KnuhResType();
//                extension.setData("");
//            }
//            String data = extension.getData();
//            log.info("server - jclient - version : {}", data);
//            log.info("local - jlcient - version : {}", appVersion);
//
//            // 데이터 요청에 실패하거나 버전이 같으면 안 함.
//            if (!data.contains("\"success\":false") && !data.equals(appVersion) && !data.isEmpty()) {
//                // 버전이 다르면, 최신 데이터(Jclient.jar) 받아오고
//                // 업데이트 후 실행하는 배치파일 실행
//
//                // 파일 업데이트 다운로드
//                boolean downloadResult = versionManagerService.downloadJclient(null, false);
//                log.info("다운로드 결과 : {}", downloadResult);
//                if (downloadResult) {
//                    Thread.sleep(1000);
//                    // 다운로드 성공 했다면, 1초 멈춘다.
//                    // 배치 파일을 실행한다.
//                    // 배치 파일 구성은
//                    // 1. 현재 실행중인 jclient를 죽인다.
//                    // 2. 이전 버전 jclient.jar를 삭제한다.
//                    // 3. 최신 버전 new_jclient.jar를 jclient.jar로 파일명을 변경한다.
//                    // 4-2. 컴퓨터를 재부팅시킨다. -> shell:startup에 jclient 실행 배치가 있을 것이므로 가능할것
//                    cmdService.runCmd("reset_jclient.bat");
//                    // 다운로드 후 프론트 bat파일을 실행시킨다.
//                    cmdService.runBat();
//                } else {
//                    // 다운로드에 실패 했다면,
//                    // 실패 했음을 서버에 던진다.
//                    HashMap<String, Object> logMap = new HashMap<>();
//                    logMap.put("message", "jclient 자동 다운로드 실패, 현재 버전 : " + version);
//
//                    try {
//                        knuhApiService.post("/api/v1/log/error", logMap);
//                    } catch (Exception ignored) {
//                    }
//                }
//            }
//        }
//
//        if (mode != null && (mode.equals("rfid") || mode.equals("rfidInside"))){
//            HashMap<String, Object> params = new HashMap<>();
//            params.put("mode", mode);
//            params.put("ip", ip);
//            params.put("target", target);
//            rfid.init(params);
//            Thread thread = new Thread(rfid);
//            thread.start();
//        } else
//            System.out.println("Default Mode");
    }
}
