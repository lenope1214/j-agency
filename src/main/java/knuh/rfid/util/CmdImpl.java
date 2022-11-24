package knuh.rfid.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CmdImpl implements CmdInterface{

    // 크롬 배치파일이 없다면 기본으로 chrome을 실행한다.
    @Value("${batUrl:start /b C://Jsolution/jclient/waitViewer.bat}")
    String batUrl;

    public String chromeReboot() {
        if (killChrome()) return runBat() ? "success chrome reboot" : "failed to open chrome";
        return "failed to kill chrome";
    }

    public boolean runBat() {
        if(this.batUrl != null){
        return runCmd(this.batUrl);
        }
        return false;
    }
}
