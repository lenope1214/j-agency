package knuh.rfid.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReqService {

    @Autowired
    private ExtApi extApi;

    public Boolean tag(HashMap<String, Object> input) {
        try{
            String target = input.get("target").toString();
            target = extApi.containHttpProtocol(target);
            URL url = new URL( target+"/api/v2/health/tag");
            System.out.println("URL : " + url);
            String[] data = input.get("data").toString().split(" ");
            HashMap<String, Object> params = new LinkedHashMap<>();
            params.put("pid", data[0]);
            params.put("ip", input.get("ip"));
            params.put("pathngnm", data[1]);
            params.put("tagno", input.get("tagno"));
   
            System.out.println("파라미터 :" + params.toString());
            String json = JsonFormatter(params);
            byte[] requsetBody = json.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); 
            conn.setRequestProperty("Accept", "application/json"); 
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(requsetBody);

            BufferedReader in = new BufferedReader((new InputStreamReader(conn.getInputStream(), "UTF-8")));

            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    } 
    public static String JsonFormatter(HashMap<String, Object> map){
        JSONObject json = new JSONObject();
        for(Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();

            json.put(key,value);
        }
        return json.toJSONString();
    }
}
