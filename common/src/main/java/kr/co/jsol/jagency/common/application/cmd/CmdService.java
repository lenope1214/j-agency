package kr.co.jsol.jagency.common.application.cmd;

import org.springframework.stereotype.Component;

@Component
public interface CmdService {
    void restartProgram();

    void rebootPc();

    void shutdownPc();

    boolean killChrome();

    boolean runCmd(String command);

    String runNGetCmd(String command);

    String chromeReboot();

    boolean runBat();

    Boolean turnOnByMac(String mac);

    Boolean turnOn(String ip, String mac);
}
