package knuh.rfid.controller;

import knuh.rfid.RFID;
import knuh.rfid.util.CmdImpl;
import knuh.rfid.util.CmdInterface;
import knuh.rfid.util.ExtApi;
import knuh.rfid.util.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;

@RestController
@Slf4j
public class Rest {

    @Value("${mode:default}")
    String mode;
    @Value("${ip:}")
    String ip;
    @Value("${target:}")
    String target;
    @Value("${batUrl:}")
    String batUrl;

    @Autowired
    private Storage storage;

    @Autowired
    private ExtApi extApi;

    @Autowired
    private CmdImpl cmd;

//    @GetMapping("/utf")
//    public ResponseEntity<?> utf(@RequestParam(name = "s",required = false) String s){
//        RFID rfid = new RFID(null);
//        String hex = "B0A1B3AAB4D9B6F3B8B6B9D9BBE7BEC6C0DAC2F7C4ABC5B8C6C4C7CFBEC6BEDFBEEEBFA9BFC0BFE4BFECC0AFC0B8C0CCC0CCBCBABAB9";
//        if(s!=null)hex = s;
//        String str = rfid.decodeHexToString(hex);
//        return new ResponseEntity<>(str, HttpStatus.OK);
//    }

    @GetMapping(value = "/poweroff")
    public void poweroff() {
        log.info("Shutdown!");
        cmd.shutdown();
    }

    @GetMapping(value = "/reboot")
    public void reboot() {
        log.info("reboot!");
        cmd.reboot();
    }

    @GetMapping(value = "/cmd/{command}")
    public ResponseEntity<?> runCmd(@PathVariable(name = "command")String command) {
        String message = "-";
        if(cmd.runCmd(command)){
            message = "작업에 성공했습니다.";
        }else{
            message= "작업에 실패했습니다.";
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    @GetMapping("/chrome/reboot")
    public ResponseEntity<?> chromeReboot() {
        log.info("chrome reboot!");
        if (ip != null && batUrl != null) {
            return new ResponseEntity<>(cmd.chromeReboot(), HttpStatus.OK);
        } else {
            return new ResponseEntity("ip와 batUrl이 지정되어있지 않습니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/jclient/download")
    public void filedownload(HttpServletResponse response, @RequestParam(name = "downloadPath", required = false) String downloadPath) throws Exception {
        URL url = null;
        InputStream in = null;
        FileOutputStream fileOutputStream = null;
        try {

            String storeUrl = "Jsolution";
            String fileName = "jclient.jar";
            // 최종적으로 C:/Jsolution/jclient.jar

            // 사이에 폴더가 없으면 에러가 발생하므로 폴더 확인
            storeUrl = storage.pathValidation(storeUrl.split("/"));

            // 폴더 + 파일명으로 저장할 위치 파일 지정
            File file = new File(storeUrl + "/" + fileName);
            fileOutputStream = new FileOutputStream(file, false); // true 시 기존 파일이 남아있음, false 시 덮어씌움

            //파일이 존재하는 위치의 URL
//                String fileUrl = target+"/api/v1/files/shop/11/test/plzme.txt/download";
            // 192.168.101.210:6060/files/download/jclient
            String fileUrl = target + "/files/download/jclient";


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
            cmd.runCmd("shutdown -r -t 0"); // 즉시 재부팅 명령어
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (in != null) in.close();
            if (fileOutputStream != null) fileOutputStream.close();
        }

    }


}
