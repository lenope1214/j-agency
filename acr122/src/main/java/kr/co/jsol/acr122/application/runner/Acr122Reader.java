package kr.co.jsol.acr122.application.runner;

import kr.co.jsol.acr122.application.Acr122Manager;
import kr.co.jsol.jagency.reader.application.runner.Readable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Acr122Reader implements Readable {

    private final Logger log = LoggerFactory.getLogger(Acr122Reader.class);
private final Acr122Manager acr122Manager;

    @Value("${acr122.debug:false}")
    String debug;

    private HashMap<String, Object> requestBody;

    public Acr122Reader(Acr122Manager acr122Manager) {
        this.acr122Manager = acr122Manager;
    }

    public void init(HashMap<String, Object> args) {
        this.requestBody = args;
    }

    @Override
    public void run() {
        log.info("Starting Acr122Reader ");

        while (acr122Manager.isConnected()) {
            try {
                acr122Manager.read();
            } catch (Exception e) {
                log.error("Error while reading card: {}", e.getMessage());
            }finally {
                // 정상 태깅 했을 때 여러번 찍히는 것 방지용
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("AFTER SEND - SLEEP ERROR - MESSAGE : {}", e.getMessage());
                }
            }
        }
    }
}
