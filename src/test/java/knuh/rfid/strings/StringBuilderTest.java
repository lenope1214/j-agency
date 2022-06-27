package knuh.rfid.strings;

import knuh.rfid.RFID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class StringBuilderTest {
    @Test
    public void 스프링빌더기본값테스트(){
        StringBuilder sb = new StringBuilder();
        System.out.println("sb.toString() = " + sb.toString());
        HashMap<String, Object> params = new HashMap<>();
        params.put("ip", "testip");
        RFID rfid = new RFID(params);
//        Thread thread = new Thread(rfid);
//        thread.start();
        Assertions.assertNotNull(sb.toString());
    }
}
