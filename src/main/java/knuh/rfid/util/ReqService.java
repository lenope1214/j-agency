package knuh.rfid.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class ReqService {

    private final ExtApi extApi;

    private final String DEFAULT_HEALTH_TARGET_URL = "/api/v2/health/tag";
    @Value("${api.url.health.tag}")
    private String healthTagUrl;

    public ReqService(@Autowired ExtApi extApi){
        this.extApi = extApi;

        log.info("ReqService 초기화 - healthTagUrl : {}", healthTagUrl);
    }

    public void tag(HashMap<String, Object> input) {
        try {
            String target = input.get("target").toString();

            target = extApi.containHttpProtocol(target);
            log.info("healthTagUrl : {}", healthTagUrl);
            if(healthTagUrl == null || healthTagUrl.isEmpty()){
                healthTagUrl = DEFAULT_HEALTH_TARGET_URL;
            }
            String apiUrl = target + healthTagUrl;
            URL url = new URL(apiUrl);
//            URL url = new URL(target + "/api/v2/health/tag");
//            URL url = new URL(target + healthTagUrl);

//            log.info("URL : " + url);
            String[] data = null;
            HashMap<String, Object> params = new LinkedHashMap<>();

            try{
                data = input.get("data").toString().split(" ");
                params.put("pid", data[0]);
                params.put("tagno", data[1]);
                params.put("pathngnm", data[2]);
            }catch (Exception ignored){}

            params.put("ip", input.get("ip"));

//            log.info("파라미터 :" + params.toString());
            String json = JsonFormatter(params);
            byte[] requestBody = json.toString().getBytes("UTF-8");
            log.info("데이터 요청 전");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpConnectionUtils.setDefaultSettings(conn);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try {
                log.info("데이터 쓰기 중");
                conn.getOutputStream().write(requestBody);
            } catch (Exception ignored) {}
            log.info("데이터 요청 종료");
//            BufferedReader in = new BufferedReader((new InputStreamReader(conn.getInputStream(), "UTF-8")));
        } catch (Exception e) {
            log.error("TAG ERROR MESSAGE : {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static String JsonFormatter(HashMap<String, Object> map) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            json.put(key, value);
        }
        return json.toJSONString();
    }
}
