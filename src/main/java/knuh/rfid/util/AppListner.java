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

    @Value("${mode}")
    String mode;
    @Value("${ip}")
    String ip;
    @Value("${target}")
    String target;
    @Value("${batUrl}")
    String batUrl;

    @Override
    public void run(String... args) throws Exception {
        if (mode != null && mode.equals("rfid")) {
            HashMap<String, Object> params = new HashMap<>();
            RFID rfid = new RFID(params);
            Thread thread = new Thread(rfid);
            thread.start();
        } else
            System.out.println("Default Mode");
    }
}
