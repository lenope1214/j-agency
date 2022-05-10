package knuh.rfid.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Slf4j
@Component
public class ExtApi {
    private final int ms = 2000;
    @Value("${target}")
    String target;

    public boolean pingCheck()  {
        String t =target.replaceAll(":[0-9]+$", "");
        log.info("핑 체크 ip : {}", target);
        log.info("핑 체크 ip parse: {}", t);
        try{
            InetAddress inet = InetAddress.getByName(t);
            // 주어진 밀리세컨드 내에 원격호스트에 접근 가능하면 true, 아니면 false
            // ms = 1/1000 second
            return inet.isReachable(ms);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
