package knuh.rfid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Rest {

    @Value("${mode}")
    String mode;
    @Value("${ip}")
    String ip;
    @Value("${target}")
    String target;
    @Value("${batUrl}")
    String batUrl;

    @GetMapping(value="/poweroff")
    public void poweroff(){
        log.info("Shutdown!");
        shutdown();
    }


    @GetMapping("/chrome/reboot")
    public ResponseEntity<?> chromeReboot(){
      log.info("chrome reboot!");
      if(ip!=null && batUrl != null){
        return new ResponseEntity<>(reboot(), HttpStatus.OK);
      }else{
          return new ResponseEntity("ip와 batUrl이 지정되어있지 않습니다.", HttpStatus.OK);
      }
    }

    public String shutdown() {
        return runCmd("shutdown -s -f")?"The machine has been shutdown!":"Failed to shutdown that machine.";
    }

    public String reboot(){
        if(killChrome())return runBat() ? "success reboot" : "failed to open chrome";
        return "failed to kill chrome";
    }

    public boolean killChrome(){
        return runCmd("TASKKILL /F /IM chrome.exe /T");
    }
    public boolean runBat() {
        return runCmd(this.batUrl);
    }



    public boolean runCmd(String command){
        try{

            // /c = 문자열로 이루어진 명령어를 실행,
            Process myProcess = Runtime.getRuntime().exec("cmd /c " + command);
            myProcess.waitFor();

            return myProcess.exitValue() == 0;
        } catch( Exception e ) {
            return false;
        }
    }
}
