package knuh.rfid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping(value="/poweroff")
    public void poweroff(){
        System.out.println("Shutdown!");
        shutdown();
    }

    @GetMapping("/chrome/reboot")
    public void chromeReboot(){
      log.info("chrome reboot!");
      if(ip!=null){
          log.info("ip : {}", ip);
      }
    }

    public static Boolean shutdown() {
        try{
            String cmd = "shutdown -s -f";

            Process myProcess = Runtime.getRuntime().exec("cmd /c " + cmd);
            myProcess.waitFor();

            if(myProcess.exitValue() == 0) {
                    System.out.println( "The machine has been shutdown!");
            } else {
                    System.out.println( "Failed to shutdown that machine.");
            }
            return true;
        } catch( Exception e ) {
            return false;
        }
    }
}
