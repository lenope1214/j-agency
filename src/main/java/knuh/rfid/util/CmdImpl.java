package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
@Component
public class CmdImpl implements CmdInterface {

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

    public Boolean turnOnByIp(String ip) {
        try {
            log.info("turnOnByIp ip : {}", ip);
            return turnOn(ip, "255.255.255.255");
        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public Boolean turnOn(String ip, String mac) throws Exception {
        final int PORT = 9; // wol 기본 포트

        // ip는 broadcast ip로 하는게 좋다.
        // ex) ip가 192.168.0.245 > 192.168.0.255 이때 서브넷 마스크에 따라 broad cast ip는 변경될 수 있음.
        String ipStr = ip;
        String macStr = mac;
        log.info("turnOn - ipStr : {}", ipStr);
        log.info("turnOn - macStr : {}", macStr);

        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
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
