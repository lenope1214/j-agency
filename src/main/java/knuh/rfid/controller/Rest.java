//package knuh.rfid.controller;
//
//import knuh.rfid.util.CmdImpl;
//import knuh.rfid.util.VersionManagerService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//
//@RestController
//@Slf4j
//public class Rest {
//
//    @Value("${mode:default}")
//    String mode;
//    @Value("${ip:localhost}")
//    String ip;
//
//    @Value("${batUrl:}")
//    String batUrl;
//
//    @Value("${ext.appVersion:}")
//    private String appVersion;
//
//
//    @Autowired
//    private VersionManagerService versionManagerService;
//
//    @Autowired
//    private CmdImpl cmd;
//
////    @GetMapping("/utf")
////    public ResponseEntity<?> utf(@RequestParam(name = "s",required = false) String s){
////        RFID rfid = new RFID(null);
////        String hex = "B0A1B3AAB4D9B6F3B8B6B9D9BBE7BEC6C0DAC2F7C4ABC5B8C6C4C7CFBEC6BEDFBEEEBFA9BFC0BFE4BFECC0AFC0B8C0CCC0CCBCBABAB9";
////        if(s!=null)hex = s;
////        String str = rfid.decodeHexToString(hex);
////        return new ResponseEntity<>(str, HttpStatus.OK);
////    }
//
//    @GetMapping("/turnon/{ip}/{mac}")
//    @ResponseStatus(HttpStatus.OK)
//    public Boolean turnOnByIpAndMac(
//            @PathVariable String ip,
//            @PathVariable String mac
//    ){
//        return cmd.turnOn(ip, mac);
//    }
//
//    @GetMapping("/turnon/{mac}")
//    @ResponseStatus(HttpStatus.OK)
//    public Boolean turnOnByMac(@PathVariable String mac){
//        return cmd.turnOnByMac(mac);
//    }
//
//    @GetMapping("/version")
//    @ResponseStatus(HttpStatus.OK)
//    public String appVersion(){
//        return appVersion;
//    }
//
//    @GetMapping(value = "/poweroff")
//    public void poweroff() {
//        log.info("Shutdown!");
//        cmd.shutdown();
//    }
//
//    @GetMapping(value = "/reboot")
//    public void reboot() {
//        log.info("reboot!");
//        cmd.reboot();
//    }
//
//    @GetMapping(value = "/cmd/{command}")
//    public ResponseEntity<?> runCmd(@PathVariable(name = "command")String command) {
//        String message = "-";
//        if(cmd.runCmd(command)){
//            message = "작업에 성공했습니다.";
//        }else{
//            message= "작업에 실패했습니다.";
//        }
//        return new ResponseEntity<>(message, HttpStatus.OK);
//    }
//
//
//    @GetMapping("/chrome/reboot")
//    public ResponseEntity<?> chromeReboot() {
//        log.info("chrome reboot!");
//        if (ip != null && !ip.isEmpty()) {
//            return new ResponseEntity<>(cmd.chromeReboot(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("ip와 batUrl이 지정되어있지 않습니다.", HttpStatus.OK);
//        }
//    }
//
//    /**
//     * Target 서버에서 jclient 최신 버전을 다운로드한다.
//     * 이때 자기 자신(jclient.jar)가 실행 중이므로 new_jclient.jar로 다운로드 받아서 저장.
//     * 저장 후 bat 파일을 실행한다.
//     *  bat 파일 로직
//     *  1. jclient.jar를 종료시키고
//     *  2. jclient.jar 를 삭제
//     *  3. new_jclient.jar를 jclient.jar로 이름 변경한다.
//     *  4. 변경된 jclient.jar를 실행하는 bat를 실행한다.
//     */
//    @GetMapping("/jclient/download")
//    public ResponseEntity<Boolean> filedownload(HttpServletResponse response, @RequestParam(name = "downloadPath", required = false) String downloadPath) throws Exception {
//        boolean result = versionManagerService.downloadJclient(downloadPath, true);
//        if(result){
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//
//}
