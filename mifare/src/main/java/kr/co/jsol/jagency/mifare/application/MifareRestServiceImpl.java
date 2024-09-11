package kr.co.jsol.jagency.mifare.application;

import kr.co.jsol.jagency.common.application.RestService;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralServerException;
import kr.co.jsol.jagency.mifare.application.dto.WriteMifareDto;
import kr.co.jsol.jagency.mifare.infrastructure.MifareRepository;
import kr.co.jsol.jagency.reader.application.dto.TagDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

@Service
public class MifareRestServiceImpl extends RestService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${mifare.use:false}")
    private Boolean isUsed;

    @Value("${app.tag.mode:read}")
    private String mode;

    private String tagUrl;

    @Value("${app.file-server-host:}")
    private String appFileServerHost;

    @Value("${app.tag.endpoint:}")
    private String tagEndpoint;

    @Value("${debug:false}")
    private String debug;

    @Value("${app.tag.roomId:}")
    private String roomId;

    private final RestTemplate restTemplate;
    private final MifareRepository mifareRepository;

    public MifareRestServiceImpl(RestTemplate restTemplate, MifareRepository mifareRepository) {
        this.restTemplate = restTemplate;
        this.mifareRepository = mifareRepository;
    }

    private boolean isDebug() {
        return this.debug.equals("true");
    }

    // 서비스 생성 후 초기화
    @PostConstruct
    public void init() {
        if (!isUsed) {
            log.info("acr122 사용 안함");
            return;
        }
        if (appFileServerHost.isEmpty()) {
            throw new GeneralServerException.InternalServerException("app.file-server-host 속성 값은 필수입니다.");
        }

        tagUrl = appFileServerHost + tagEndpoint;

        log.info("ReqService 초기화");
        log.info("tagHost : {}", tagUrl);
        tagUrl = containHttpProtocol(tagUrl);
        // /로 끝나면 제거
        if (tagUrl.endsWith("/")) {
            tagUrl = tagUrl.substring(0, tagUrl.length() - 1);
        }

        // roomId
        if (roomId == null || roomId.isEmpty()) {
            throw new GeneralServerException.InternalServerException("roomId는 필수입니다.");
        }
    }

    public void tag(TagDto tagDto) {
//        if (!isUsed) {
//            log.info("acr122 사용 안함");
//            return "acr122 사용 안함";
//        }

        try {
            log.info("API URL : {}", tagUrl);
            log.info("tagNo: {}, roomId: {}", tagDto.getTagNo(), tagDto.getRoomId());

            restTemplate.postForEntity(tagUrl, tagDto, Boolean.class);

            // 에러는 exception에서 잡힘
            log.info("태깅 성공");
        } catch (Exception e) {
            // 에러 형식 : STATUS : "{"property":"value",...}"
            // 여기서 property를 추출하여 사용하면 됨
            String errorBody = e.getMessage();

            // 만약  HTTP_STATUS(4xx~5xx) : "{"property":"value",...}" 의 형태라면 서버 에러이므로 처리
            if (!errorBody.contains(": {")) {
                log.error("MESSAGE : {}", errorBody);
                return;
            }

            // : 이후로 json 형식이므로 : 이후로 자르고
            String errorJson = errorBody.substring(errorBody.indexOf(":") + 1);
            // json 형식으로 변환
            HashMap<String, Object> errorMap = new HashMap<>();
            Arrays.stream(errorJson.split(",")).forEach(
                    s -> {
                        String[] split = s.split(":");
                        errorMap.put(split[0].replace("\"", ""), split[1].replace("\"", ""));
                    }
            );

            // errorMap logging
            errorMap.forEach((key, value) -> log.error("태깅 실패 사유 - {}: {}", key, value));

            //TODO
        }

    }

    public void write(WriteMifareDto writeMifareDto) throws IOException {
        if (mode.equals("write")) {
            mifareRepository.writeToCards(writeMifareDto);
        }
    }

    public void read() throws IOException {
        mifareRepository.read(sendServer());
    }

    public boolean isConnected() {
        return mifareRepository.isConnected();
    }

    //TODO private로 변경
    public @NotNull Consumer<String> sendServer() {
        return (arg) -> {
            log.info("서버로 데이터를 전송합니다 데이터: {}", arg);
            tag(new TagDto(arg, roomId));
        };
    }
}
