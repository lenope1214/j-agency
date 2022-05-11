package knuh.rfid.util;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import knuh.rfid.RFID;

@Component
@Slf4j
public class AppListner implements CommandLineRunner {

    // : 을 붙여서   java jclient --mode 이런 properties를 입력 안 했을때 오류 나지 않도록 설정해준다.
    @Value("${mode}")
    String mode;
    @Value("${ip}")
    String ip;


    @Override
    public void run(String... args) throws Exception {
        if (mode != null && mode.equals("rfid")) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("ip", ip);
            RFID rfid = new RFID(params);
            Thread thread = new Thread(rfid);
            thread.start();
        } else
            System.out.println("Default Mode");
    }
}
