package kr.co.jsol.jagency.ccrhfn.application;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class ReqService {

    private final ExtApi extApi;

    private final String DEFAULT_HEALTH_TARGET_URL = "/api/v2/health/tag";
    @Value("${api.url.health.tag}")
    private String healthTagUrl;

    @Value("${debug:false}")
    String debug;

    private boolean isDebug() {
        return this.debug.equals("true");
    }

    public ReqService(@Autowired ExtApi extApi) {
        this.extApi = extApi;

        log.info("ReqService 초기화 - healthTagUrl : {}", healthTagUrl);
    }

    public void tag(HashMap<String, Object> input) {
        try {
            String target = input.get("target").toString();

            target = extApi.containHttpProtocol(target);
            if (isDebug()) log.info("healthTagUrl : {}", healthTagUrl);
            if (healthTagUrl == null || healthTagUrl.isEmpty()) {
                healthTagUrl = DEFAULT_HEALTH_TARGET_URL;
            }
            String apiUrl = target + healthTagUrl;
            URL url = new URL(apiUrl);
//            URL url = new URL(target + "/api/v2/health/tag");
//            URL url = new URL(target + healthTagUrl);

//            log.info("URL : " + url);
            String[] data = null;
            HashMap<String, Object> params = new LinkedHashMap<>();

            try {
                data = input.get("data").toString().split(" ");
                params.put("pid", data[0]);
                params.put("tagno", data[1]);
                params.put("pathngnm", data[2]);
            } catch (Exception ignored) {
            }

            params.put("ip", input.get("ip"));

//            log.info("파라미터 :" + params.toString());
            String json = JsonFormatter(params);
            byte[] requestBody = json.toString().getBytes("UTF-8");
            if (isDebug()) log.info("데이터 요청 전");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpConnectionUtils.setDefaultSettings(conn);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try {
                if (isDebug()) log.info("데이터 쓰기 중");
                conn.getOutputStream().write(requestBody);
            } catch (Exception ignored) {
            }
            if (isDebug()) log.info("데이터 요청 종료");

            // 주의))) input stream 주석처리하면 데이터 통신이 진행되지 않음.
            BufferedReader in = new BufferedReader((new InputStreamReader(conn.getInputStream(), "UTF-8")));
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
