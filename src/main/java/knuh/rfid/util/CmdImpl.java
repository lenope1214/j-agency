package knuh.rfid.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CmdImpl implements CmdInterface{
    @Value("${batUrl}")
    String batUrl;

    public String reboot() {
        if (killChrome()) return runBat() ? "success reboot" : "failed to open chrome";
        return "failed to kill chrome";
    }

    public boolean runBat() {
        return runCmd(this.batUrl);
    }
}
