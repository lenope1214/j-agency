package kr.co.jsol.jagency.ccrhfn.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Slf4j
@Service
public class ExtApi {
    @Value("${target:}")
    String target;

    public boolean pingCheck() {
        String t = target.replaceAll(":[0-9]+$", ""); // 포트번호 삭제
        t = t.replaceAll("\\\\", "/"); // \ -> /로 변경
        t = t.replaceAll("http.*?//", " "); // http(s):// 삭제
        try {
            InetAddress inet = InetAddress.getByName(t);
            // 주어진 밀리세컨드 내에 원격호스트에 접근 가능하면 true, 아니면 false
            // ms = 1/1000 second
            int ms = 2000;
            return inet.isReachable(ms);
        } catch (Exception e) {
            return false;
        }
    }


    public String containHttpProtocol(String url) {
        // http(s) 프로토콜 설정이 없으면 기본으로 http 붙여줌
//        log.info("containHttpProtocol fileUrl : {}", fileUrl);
        log.info("{} is url starts http ? {}", url, isStartHttp(url));
        if (isStartHttp(url)) {
            return url;
        }
        url = "http://" + url;
        return url;
    }

    private boolean isStartHttp(String url) {
        return url.startsWith("http");
    }
}
