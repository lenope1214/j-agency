package knuh.rfid.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CmdImpl implements CmdInterface{
    @Value("${batUrl:start chrome.exe}")
    String batUrl;

    public String chromeReboot() {
        if (killChrome()) return runBat() ? "success reboot" : "failed to open chrome";
        return "failed to kill chrome";
    }

    public boolean runBat() {
        if(this.batUrl != null){
        return runCmd(this.batUrl);
        }
        return false;
    }
}
