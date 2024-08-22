package kr.co.jsol.jagency.common.application.cmd;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

// 리눅스 기반 PC에서 커맨드라인 명령어를 내리기 위한 서비스
@Slf4j
public class LinuxCommandService implements CmdService {
    // 크롬 배치파일이 없다면 기본으로 chrome을 실행한다.
    private String batUrl;

    private String restartFileName = "restart.sh";

    public LinuxCommandService(
            String batUrl
    ) {
        log.info("LinuxCommandService 생성자 실행");
        log.info("batUrl : {}", batUrl);
        this.batUrl = batUrl;
    }

    public void restartProgram() {
        if (restartFileName != null) {
            runCmd("sh " + restartFileName);
        }
    }

    public void rebootPc() {
        runCmd("shutdown -r now");
    }

    public void shutdownPc() {
        // -h는 halt의 약자로 시스템을 멈추고 종료하라는 의미입니다.
        runCmd("shutdown -h now");
    }

    public boolean killChrome() {
        return runCmd("killall chrome");
    }


    public boolean runCmd(String command) {
        try {
            log.info("runCmd command : {}", command);
            // /c = 문자열로 이루어진 명령어를 실행,
            new ProcessBuilder("/bin/sh", "-c", command).start();
//            Process myProcess = Runtime.getRuntime().exec(runCommand);
//            myProcess.waitFor();
//
//            return myProcess.exitValue() == 0;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String runNGetCmd(String command) {
        try {

            // /c = 문자열로 이루어진 명령어를 실행,
            Process myProcess = Runtime.getRuntime().exec("/bin/bash " + command);
            myProcess.waitFor();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(myProcess.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder sb = new StringBuilder();
            sb.append(command);
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String chromeReboot() {
        if (killChrome()) return runBat() ? "success chrome reboot" : "failed to open chrome";
        return "failed to kill chrome";
    }

    public boolean runBat() {
        if (this.batUrl != null) {
            log.info("batUrl : {}", batUrl);
            return runCmd(this.batUrl);
        }
        return false;
    }

    public Boolean turnOnByMac(String mac) {
        try {
            log.info("turnOnByIp mac : {}", mac);
            return turnOn("255.255.255.255", mac);
        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public Boolean turnOn(String ip, String mac) {
        final int PORT = 9; // wol 기본 포트

        // ip는 broadcast ip로 하는게 좋다.
        // ex) ip가 192.168.0.245 > 192.168.0.255 이때 서브넷 마스크에 따라 broad cast ip는 변경될 수 있음.

        log.info("turnOn - ipStr : {}", ip);
        log.info("turnOn - macStr : {}", mac);

        try {
            byte[] macBytes = getMacBytes(mac);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ip);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            log.info("wol socket 전송중..");
            socket.send(packet);
            socket.close();
            log.info("wol socket 전송 종료");
            return true;
        } catch (Exception e) {
            return false;
            // System.exit(1);
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6)
            throw new IllegalArgumentException("Invalid MAC address.");
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }

        return bytes;
    }
}
