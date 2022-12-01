package knuh.rfid;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

import jaco.mp3.player.MP3Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import knuh.rfid.util.ReqService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@RequiredArgsConstructor
@Slf4j
@Component
public class RFID implements Runnable{
    // private final ApplicationArguments appArgs;
    private HashMap<String, Object> param;

    private final ReqService send;

    @Value("${debug:false}")
    String debug;
    private boolean isDebug(){
        return this.debug.equals("true");
    }

    public void init(HashMap<String, Object> args){
        this.param = args;
    }

    // RFIDLibrary r = RFIDLibrary.INSTANCE;
    @Override
    public void run() {
        log.info("Starting main!! ");
        
		// 클래스 생성 초기화하
		RFIDLibrary r = RFIDLibrary.INSTANCE;

        while(true) {
		    // 리더기가 연결되어 있는지
            if(r.ccr_device_find()){
                try {
                    if(isDebug())log.info("읽기 시작");
                    String read = Reader();

                    if(read != null && read.length()>0 && read.split(" ").length == 3){
                        log.info("읽은 데이터 : {}", read);
                        this.GoodBeep();



                        param.put("data", read);

//                        if(send == null){
//                            send = new ReqService();
//                        }
                        if(isDebug())log.info("읽은 데이터 전송 시작");
                        send.tag(param);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }else{
                log.error("RFID READER DEVICE IS NOT CONNECTED!");
            }
            try{
                Thread.sleep(500);
            }catch(Exception e){
                log.error("RFID.JAVA - SLEEP ERROR - MESSAGE : {}",e.getMessage());
            }
        }
    }


//    public String ReadUIDAuto() {
//        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
//        String protocol = "44002020202020202020202020202020202053";
//        String sendProtocol = new String(protocol);
//        return read(sendProtocol);
//    }
//
//    public String ReadUID15() {
//        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
//        String protocol = "44022020202020202020202020202020202053";
//        String sendProtocol = new String(protocol);
//        return read(sendProtocol);
//    }
//
//    public String ReadUIDmi() {
//        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
//        String protocol = "44012020202020202020202020202020202053";
//        String sendProtocol = new String(protocol);
//        return read(sendProtocol);
//    }

    public String Reader() {
        // 52가 Read 하겠다는 뜻
        // 5201 -> Read 01 섹터
        // mifare 섹터는 00~03 섹터로 구성되어 있음
        // 00, 03은 사용하지 않음

        String sector01 = "52012020202020202020202020202020202053";
        String sector02 = "52022020202020202020202020202020202050";
//    log.info("read~~");
        String sendProtocol = sector01;
        String result = "";
//        log.info("read 01");
        String r01 = read(sendProtocol);
//        log.info("res 01 : {}", result);
        if(r01 != null){
            result += r01;
        }

        sendProtocol = new String(sector02);
//        log.info("read 02");
        String r02 = read(sendProtocol);
//        log.info("res 02 : {}", r02);
        if(r02 != null) {
            result += r02;
        }
//        log.info("result : {}", result);
        // 데이터 포매팅
        result = result.replaceAll(" ", "");
        result = result.replaceAll("_", " ");
//        log.info("변환 후 : {}", result);
        return result;
    }

    private String read(String protocol){
        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
        // 52가 Read 하겠다는 뜻
        // 5201 -> Read 01 섹터
        // 섹터는 01~04섹터로 구성되어 있음
        String sendProtocol = new String(protocol);
        byte[] output = new byte[39];
        try{
            rfid.ccr_data_transceive_ex(sendProtocol, output);
            String receiveProtocol = new String(output);
            // return new String(bytes, StandardCharsets.US_ASCII);
            // 실패 프토토콜 플래그
            if(receiveProtocol.substring(0, 2).equals("45"))
                return "";
            return decodeHexToString(new String(output));
        }catch(Exception e){
            System.out.println("error : " + e);
            this.BadBeep();
            return "";
        }
    }

    public File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile(String.valueOf(inputStream.hashCode()), ".tmp");
        // jvm 종료 시 같이 지워지도록
        tempFile.deleteOnExit();

        copyInputStreamToFile(inputStream, tempFile);

        return tempFile;
    }

    public void GoodBeep() {
        //기존에는 rfid reader 기기 내에 있는 비프음이 들리게 했다.
        // 그러나 비프음이 너무 작아 불편하다는 의견이 있어서 주석처리함.

//        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
//        String sendProtocol = new String("43012020202020202020202020202020202042");
//        byte[] output = new byte[39];
//        try{
//            rfid.ccr_data_transceive_ex(sendProtocol, output);
//            System.out.println("GOOD Beep");
//        }catch(Exception e){
//            System.out.println("error : " + e);
//        }



        // jaco mp3 player 를 이용한 비프음
        try {
            InputStream inputStream = new ClassPathResource("beep.mp3").getInputStream();
            File file = convertInputStreamToFile(inputStream);
            MP3Player mp3Player = new MP3Player(file);
            mp3Player.play();

            // 아래 실행 확인이 없으면 소리가 나지 않는다.
            // 필수 처리############################################
            while (!mp3Player.isStopped()) {
                Thread.sleep(10);
            }
            // 필수 처리############################################
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void BadBeep() {
        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
        String sendProtocol = new String("43022020202020202020202020202020202041");
        byte[] output = new byte[39];
        try{
            rfid.ccr_data_transceive_ex(sendProtocol, output);
            System.out.println("BAD Beep");
        }catch(Exception e){
            System.out.println("error : " + e);
        }
    }

    public String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();

        for(byte b : bytes){
            sb.append(String.format("%02X", b&0xff));
        }

        return sb.toString();
    }

    public String decodeHexToString(String hexString){

        try {
            StringBuilder sb = new StringBuilder();
            char[] chars = hexString.toCharArray();
            //                log.info("chars : {}", chars);
//                log.info("chars.length : {}", chars.length);

            // 0~3번튼 태그 결과 확인용
            // 4번부터 가져오는게 맞다.
            for(int i = 4 ; i < 32 && i<chars.length ; i++){
                sb.append(chars[i]);
            }
            //                log.info("sb : {}", sb);
            byte[] bytes  = Hex.decodeHex(sb.toString().toCharArray());
            //                log.info("bytes : {}", bytes);
            //                log.info("decodeHexToString result : {}", result);
            return new String(bytes, "euc-kr");
        } catch (Exception e) {
           log.error(e.getMessage());
           e.printStackTrace();
           return null;
        }
    }
}
