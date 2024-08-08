package kr.co.jsol.jagency.reader.application;

import kr.co.jsol.jagency.reader.application.dto.TagDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public interface TagService {
    final Logger log = LoggerFactory.getLogger(TagService.class);

    public void tag(TagDto tagDto);

    public default boolean pingCheck(String target) {
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


    public default String containHttpProtocol(String url) {
        // http(s) 프로토콜 설정이 없으면 기본으로 http 붙여줌
//        log.info("containHttpProtocol fileUrl : {}", fileUrl);
        log.info("{} is url starts http ? {}", url, isStartHttp(url));
        if (isStartHttp(url)) {
            return url;
        }
        url = "http://" + url;
        return url;
    }

    default boolean isStartHttp(String url) {
        return url.startsWith("http");
    }
}
