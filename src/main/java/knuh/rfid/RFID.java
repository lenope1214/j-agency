package knuh.rfid;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import knuh.rfid.util.ReqService;

@Slf4j
public class RFID implements Runnable{
    // private final ApplicationArguments appArgs;
    private final HashMap<String, Object> param;


    @Autowired
    public RFID(HashMap<String, Object> args){
        param = args;
        // ModelMapper modelMapper = new ModelMapper();
        // param = modelMapper.map(args, ParamDto.Rfid.class);
    }

    public static void getPaths() {
		String path = System.getProperty("java.library.path");
		Arrays.stream(path.split(";")).forEach(s -> {
			System.out.println(s);
		});
	}

    // RFIDLibrary r = RFIDLibrary.INSTANCE;
    @Override
    public void run() {
        log.info("Starting main!! ");
        
		// 클래스 생성 초기화하
		RFIDLibrary r = RFIDLibrary.INSTANCE;

		// 리더기가 연결되어 있는지
		log.info("RFID READER DEVICE : " + r.ccr_device_find());
		
        while(r.ccr_device_find()) {
            try {
//                String readStr00 = Reader("00");
                String readStr01 = Reader("01");

                if(readStr01 != null){
                    log.info("readStr01 : {}", readStr01);
                    this.GoodBeep();
                    ReqService send = new ReqService();
//                    param.put("data00", readStr00);
                    param.put("data", readStr01);
                    send.tag(param);
                    try{
                        Thread.sleep(5000);
                    }catch(Exception e){
                        e.printStackTrace();
                        log.error("RFID.JAVA - ERROR MESSAGE : {}",e.getMessage());
                    }
                }
            } catch (Exception e) {
               System.out.println(e);
            }
        }
    }


    public String ReadUIDAuto() {
        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
        String protocol = "44002020202020202020202020202020202053";
        String sendProtocol = new String(protocol);
        return read(sendProtocol);
    }

    public String ReadUID15() {
        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
        String protocol = "44022020202020202020202020202020202053";
        String sendProtocol = new String(protocol);
        return read(sendProtocol);
    }

    public String ReadUIDmi() {
        // 44 -> D UID 구해오는 key, 00= auto  01 : mifare  02 : 15693-icode
        String protocol = "44012020202020202020202020202020202053";
        String sendProtocol = new String(protocol);
        return read(sendProtocol);
    }

    public String Reader(String sector) {
        // 52가 Read 하겠다는 뜻
        // 5201 -> Read 01 섹터
        // mifare 섹터는 00~03 섹터로 구성되어 있음
        // 15693 섹터는 00~01 섹터로 구성되어 있음
        if(sector == null)sector = "01"; // 기본으로 01을 사용함
        String protocol = "52"+sector+"2020202020202020202020202020202053";
        String sendProtocol = new String(protocol);
        return read(sendProtocol);
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
                return null;
            return decodeHexToString(new String(output));
        }catch(Exception e){
            System.out.println("error : " + e);
            this.BadBeep();
            return null;
        }
    }

    public void GoodBeep() {
        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
        String sendProtocol = new String("43012020202020202020202020202020202042");
        byte[] output = new byte[39];
        try{
            rfid.ccr_data_transceive_ex(sendProtocol, output);
            System.out.println("GOOD Beep");
        }catch(Exception e){
            System.out.println("error : " + e);
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
            if(chars != null) {
                log.info("chars : {}", chars);
                log.info("chars.length : {}", chars.length);
            }

            // 0~3번튼 태그 결과 확인용
            // 4번부터 가져오는게 맞다.
            for(int i = 4 ; i < 32 && i<chars.length ; i++){
                sb.append(chars[i]);
            }
            if( sb != null) {
                log.info("sb : {}", sb);
            }
            byte[] bytes  = Hex.decodeHex(sb.toString().toCharArray());
            if(bytes != null) {
                log.info("bytes : {}", bytes);
            }
            String result = new String(bytes, "euc-kr");//euc-kr로 인코딩 되어있음.
            if(result != null) {
                log.info("decodeHexToString result : {}", result);
            }
            return result;
        } catch (Exception e) {
           log.error(e.getMessage());
           e.printStackTrace();
           return null;
        }
    }
}
