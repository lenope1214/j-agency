package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ExtApi {
    private final int ms = 2000;
    @Value("${target}")
    String target;

    public boolean pingCheck()  {
        String t =target.replaceAll(":[0-9]+$", ""); // 포트번호 삭제
        t = t.replaceAll("\\\\", "/"); // \ -> /로 변경
        t = t.replaceAll("http.*?//", " "); // http(s):// 삭제
        try{
            InetAddress inet = InetAddress.getByName(t);
            // 주어진 밀리세컨드 내에 원격호스트에 접근 가능하면 true, 아니면 false
            // ms = 1/1000 second
            return inet.isReachable(ms);
        }catch (Exception e){
            return false;
        }
    }


    public String containHttpProtocol(String fileUrl){
        // http(s) 프로토콜 설정이 없으면 기본으로 http 붙여줌
        if(!fileUrl.matches("^(https?)://")){
            fileUrl = "http://"+fileUrl;
        }
        return fileUrl;
    }
}
