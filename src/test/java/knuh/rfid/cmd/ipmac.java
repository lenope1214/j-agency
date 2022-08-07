package knuh.rfid.cmd;

import knuh.rfid.RFID;
import knuh.rfid.util.CmdImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootTest
@Slf4j
public class ipmac {

    @Test
    public void ip로mac주소가져오기() {
        CmdImpl cmdImpl = new CmdImpl();
        String resStr = "";
        String ip = "192.168.219.1";
        String mac = "";

        /*
        resStr = cmdImpl.runNGetCmd("ping "+ ip);
        log.info("resStr(ping) : {}", resStr);
        */

        resStr = cmdImpl.runNGetCmd("arp -a");
        log.info("resStr(arp -a) : {}", resStr);

        /*
        String[] space = resStr.split(" ");
        System.out.println("--------------- space ---------------");
        Arrays.stream(space).forEach(t->log.info(t));
        */

        // 개행 기준 가능
        String[] nl = resStr.split("\\n");
        System.out.println("--------------- new line ---------------");
        Arrays.stream(nl).forEach(t -> log.info(t));
        for (String str :
                nl) {

            String macAddr = str.length() > 41 ? "("+str.substring(24, 41)+")" : null;
                log.info("macAddr : {}", macAddr);
            if (str.contains(ip)) {
                log.info("mac line : {}", str);
                mac = macAddr;
            }
        }
        System.out.println("mac = " + mac);


        // 탭은 안되네,,~
        /*
        String[] tap = resStr.split("\\t");
        System.out.println("--------------- tap ---------------");
        Arrays.stream(tap).forEach(t->log.info(t));
        */


        // sb가 null인지 체크
        Assertions.assertEquals(mac.equals("(80-ca-4b-45-9d-9d)"), true);
    }

    @Test
    public void ping테스트0() {
        CmdImpl cmdImpl = new CmdImpl();
        String resStr = "";

        resStr = cmdImpl.runNGetCmd("ping 192.168.219.222");
        log.info("resStr : {}", resStr);

        // 받음이 0인지 확인
        Assertions.assertEquals(resStr.contains("받음 = 0"), true);
    }

    @Test
    public void ping테스트1() {
        CmdImpl cmdImpl = new CmdImpl();
        String resStr = "";

        resStr = cmdImpl.runNGetCmd("ping https://naver.com");
        log.info("resStr : {}", resStr);

        // 받음이 0인지 확인
        Assertions.assertEquals(resStr.contains("받음 = 0"), true);
    }
}
