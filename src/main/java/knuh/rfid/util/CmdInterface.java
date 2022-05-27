package knuh.rfid.util;

public interface CmdInterface {
    default String reboot() {
        return runCmd("shutdown -r -t 0") ? "The machine has been shutdown!" : "Failed to shutdown that machine.";
    }

    default String shutdown() {
        return runCmd("shutdown -s -f 0") ? "The machine has been shutdown!" : "Failed to shutdown that machine.";
    }

    default boolean killChrome() {
        return runCmd("TASKKILL /F /IM chrome.exe /T");
    }


    default boolean runCmd(String command) {
        try {

            // /c = 문자열로 이루어진 명령어를 실행,
            Process myProcess = Runtime.getRuntime().exec("cmd /c " + command);
            myProcess.waitFor();

            return myProcess.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
