package knuh.rfid.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;

public class WolTest {

    @Test
    public void turnOn() {

        // ip는 broadcast ip로 하는게 좋다.
        final String ip = "192.168.0.255";
//        final String ip = "255.255.255.255";

        // mac 주소
//        final String mac = "00-E0-4C-D0-8C-03";
        final String mac = "00:E0:4C:D0:8C:03";

//        final String mac = "00-e0-4c-d0-8c-03";
//        final String mac = "00:e0:4c:d0:8c:03";

        // 해당 피시 포트
        final int PORT = 7; // wol 기본 포트


        System.out.println("turnOn - ipStr : " + ip);
        System.out.println("turnOn - macStr : " + mac);
        System.out.println("turnOn - port : " + PORT);

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
            socket.send(packet);
            socket.close();

            System.out.println("Wake-on-LAN packet sent.");
        } catch (Exception e) {
            System.err.println("Failed to send Wake-on-LAN packet: + e");
            assert false;
        }
        assertThat(true).isTrue();
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
