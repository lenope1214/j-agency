package knuh.rfid.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class KnuhApiServiceImpl implements ExternalApiService, KnuhApiService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Gson gson;
    private JsonObject jsonObject;

    @Autowired
    private ExtApi extApi;
    //    private final String serverUrl = "http://127.0.0.1:3001/api/inout";
    @Value("${target:http://127.0.0.1:8083}")
    String target;

    @PostConstruct
    private void setup() {
        gson = new GsonBuilder().create();
    }

    @Override
    public String get(String url) {
        return get(url, null);
    }

    @Override
    public String get(String url, String authToken) {
        return requestAfterSetting(url, null, "GET", authToken);
    }

    @Override
    public String post(String url, Map<String, Object> body) {
        return post(url, body, null);
    }

    public String post(String url, Map<String, Object> body, String authToken) {
        return requestAfterSetting(url, body, "POST", authToken);
    }

    public String requestAfterSetting(String url, Map<String, Object> body, String method, String authToken) {
        Map<String, String> requestHeaders = new HashMap<>(); // 키 밸류 (json)으로 보내기.
        requestHeaders.put("Accept", "application/json");
        if(authToken!=null){
            logger.info("requestAfterSetting - header settings - x-auth-token : {}", authToken);
            requestHeaders.put("x-auth-token", authToken);
        }

        gson = new GsonBuilder().create();
        Stream<String> bodyKeys = null;
        if (body != null) {
            logger.info("requestAfterSetting - header settings - Content-Type : application/json");
            requestHeaders.put("Content-Type", "application/json");
            bodyKeys = body.keySet().stream();
            logger.info("requestAfterSetting - body settings - bodyKeys : {}", bodyKeys);
            jsonObject = parseBody(body, bodyKeys);
            logger.info("requestAfterSetting - body settings - jsonObject : {}", jsonObject);
        }

        // ext api service가 autowired 안되면 새로 객체 생성해줌
        if(extApi == null)extApi = new ExtApi();
        // target에 http:// 가 없으면 붙여주는 설정 추가
        target = extApi.containHttpProtocol(target);
        
        // 요청 url 설정
        url = target + url;
        logger.info("requestAfterSetting - request url : {}", url);

        String responseBody = null;
        try {
            logger.info("jclient -> knuh backend [" + method + "] 요청 시작");
            responseBody = request(url, requestHeaders, method, gson.toJson(jsonObject));
            logger.info("jclient -> knuh backend [" + method + "] 요청 완료");
        } catch (Exception ex) {
            if (ex != null) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                responseBody = null;
            }
        }

        return responseBody;

    }

}
