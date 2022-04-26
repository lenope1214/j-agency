package knuh.rfid.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Rest {
    
    @GetMapping(value="/poweroff")
    public void poweroff(){
        System.out.println("Shutdown!");
        shutdown();
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
