package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ExtApiTest {

    @Test
    @DisplayName("containHttpProtocol 테스트")
    void containHttpProtocol() {
        ExtApi api = new ExtApi();
        String knuh01 = api.containHttpProtocol("http://board031.knuh.kr");
        String knuh02 = api.containHttpProtocol("https://board031.knuh.kr");
        String knuh03 = api.containHttpProtocol("board031.knuh.kr");
        String ip01 = api.containHttpProtocol("http://192.168.101.210:5114");
        String ip02 = api.containHttpProtocol("192.168.101.210:5114");

        log.info(knuh01);
        log.info(knuh02);
        log.info(knuh03);
        log.info(ip01);
        log.info(ip02);

        assertEquals(knuh01, "http://board031.knuh.kr");
        assertEquals(knuh02, "https://board031.knuh.kr");
        assertEquals(knuh03, "http://board031.knuh.kr");
        assertEquals(ip01, "http://192.168.101.210:5114");
        assertEquals(ip02, "http://192.168.101.210:5114");
    }
}