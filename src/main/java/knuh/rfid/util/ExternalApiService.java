package knuh.rfid.util;


import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public interface ExternalApiService {
    Logger logger = LoggerFactory.getLogger(ExternalApiService.class);


    default String request(String apiUrl, Map<String, String> requestHeaders, String Method, String body) {
        int responseCode = 400;
        logger.info("요청 url : " + apiUrl);
        HttpURLConnection con = connect(apiUrl.trim());

//        String redirect = con.getHeaderField("Location"); // https://m-call.kro.kr 로 요청할땐 필요했는데.. 이렇게 하면 SSL 오류가 발생.
//        con = getLocationHttpCon(con, redirect);
        String s = null;
        try {
            con.setRequestMethod(Method);
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            if (body != null && !body.equals("null") && !body.isEmpty()) {
                con.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
                try (OutputStream os = con.getOutputStream()) {
                    byte request_data[] = body.getBytes("utf-8");
                    os.write(request_data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            responseCode = con.getResponseCode();
            InputStream inputStream = null;
            if (responseCode >= 200 && responseCode < 400) {
                inputStream = con.getInputStream();
                s = readBody(inputStream);
            } else {
                inputStream = con.getErrorStream();
                s = readBody(inputStream);
            }
        } catch (Exception e) {
            logger.error("server error message : {}", e);
        } finally {
            if (con != null) con.disconnect();
            return s;
        }
    }

    default HttpURLConnection getLocationHttpCon(HttpURLConnection con, String redirect) {
        try {
            if (redirect != null) {
                con = (HttpURLConnection) new URL(redirect).openConnection();
            }
            return con;
        } catch (Exception e) {
            throw new RuntimeException("redirect location 연결에 실패했습니다. : " + redirect, e);
        }
    }

    default String request(String apiUrl, Map<String, String> requestHeaders, String Method) {
        return request(apiUrl, requestHeaders, Method, null);
    }

    default HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    default String readBody(InputStream body) {
        if (body != null) {
            InputStreamReader streamReader = new InputStreamReader(body, StandardCharsets.UTF_8);

            try (
                    BufferedReader lineReader = new BufferedReader(streamReader)
            ) {
                StringBuilder responseBody = new StringBuilder();

                String line;
                while ((line = lineReader.readLine()) != null) {
                    responseBody.append(line);
                }

                return responseBody.toString();
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
            }
        } else {
            return "";
        }

    }

    default JsonObject parseBody(Map<String, Object> body, Stream<String> bodyKeys) {
        if (body == null || bodyKeys == null) return null;
        JsonObject jsonObject = new JsonObject();
        bodyKeys.forEach((key) -> {
            Object data = body.get(key);
            if (data == null) {
                return;
            }
            if (data.getClass() == Long.class) {
                jsonObject.addProperty(key, ((Long) data).longValue());
            } else if (data.getClass() == String.class) {
                jsonObject.addProperty(key, data.toString());
            } else if (data.getClass() == Integer.class) {
                jsonObject.addProperty(key, ((Integer) data).intValue());
            } else if (data.getClass() == Boolean.class) {
                jsonObject.addProperty(key, ((Boolean) data).booleanValue());
            } else if (data.getClass() == Character.class) {
                jsonObject.addProperty(key, ((Character) data).charValue());
            }
        });
        return jsonObject;
    }

    default void allowMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
