package kr.co.jsol.jagency.reader.application;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Component
public class CmdImpl implements CmdInterface {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    //TODO 배치파일 경로는 서버에서 받아오도록 수정,
    // 크롬 배치파일이 없다면 기본으로 chrome을 실행한다.
    @Value("${batUrl:start /b C://Jsolution/jclient/waitViewer.bat}")
    String batUrl;

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


    public Boolean turnOn(String ip, String mac){
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
