package kr.co.jsol.jagency.acr122.application.runner;

import kr.co.jsol.jagency.acr122.infrastructure.Acr122Repository;
import kr.co.jsol.jagency.reader.application.runner.Readable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Acr122Reader implements Readable {

    private final Logger log = LoggerFactory.getLogger(Acr122Reader.class);
    private final Acr122Repository acr122Repository;

    @Value("${acr122.debug:false}")
    Boolean debug;

    private HashMap<String, Object> requestBody;

    public Acr122Reader(Acr122Repository acr122Repository) {
        this.acr122Repository = acr122Repository;
    }

    public void init(HashMap<String, Object> args) {
        this.requestBody = args;
    }

    @Override
    public void run() {
        log.info("Starting Acr122Reader ");

        while (true) {
            try {
                if (acr122Repository.isConnected()) {
                    log.info("Connected to ACR122");
                    acr122Repository.read();
                }
            } catch (Exception e) {
                log.error("Error while reading card: {}", e.getMessage());
            } finally {
                // 정상 태깅 했을 때 여러번 찍히는 것 방지용
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    log.error("AFTER SEND - SLEEP ERROR - MESSAGE : {}", e.getMessage());
                }
            }
        }
    }
}
