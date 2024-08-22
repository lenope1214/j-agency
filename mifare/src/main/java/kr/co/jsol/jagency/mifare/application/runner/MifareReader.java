package kr.co.jsol.jagency.mifare.application.runner;

import kr.co.jsol.jagency.mifare.application.MifareRestServiceImpl;
import kr.co.jsol.jagency.reader.application.runner.Readable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class MifareReader implements Readable {

    private final Logger log = LoggerFactory.getLogger(MifareReader.class);
    private final MifareRestServiceImpl mifareTagService;

    @Value("${mifare.debug:false}")
    Boolean debug;

    private HashMap<String, Object> requestBody;

    public MifareReader(MifareRestServiceImpl mifareTagService) {
        this.mifareTagService = mifareTagService;
    }

    public void init(HashMap<String, Object> args) {
        this.requestBody = args;
    }

    @Override
    public void run() {
        log.info("Starting Acr122Reader ");

//        TODO tag test 테스트 후 제거
//        mifareRepository.sendServer().accept("test");

        while (true) {
            try {
                if (mifareTagService.isConnected()) {
                    log.info("Connected to ACR122");
                    mifareTagService.read();
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
