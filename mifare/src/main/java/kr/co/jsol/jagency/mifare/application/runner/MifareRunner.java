package kr.co.jsol.jagency.mifare.application.runner;

import kr.co.jsol.jagency.reader.application.runner.TagRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MifareRunner implements TagRunner {

    private final Logger log = LoggerFactory.getLogger(MifareRunner.class);

    // false시 RFID 사용X
    @Value("${mifare.use:false}")
    String use;

    private final MifareReader mifareReader;

    public MifareRunner(MifareReader mifareReader) {
        this.mifareReader = mifareReader;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("acr122 use : {}", use);

        if (use.equals("true")) {
            Thread thread = new Thread(mifareReader);
            thread.start();
        }
    }
}
