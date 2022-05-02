package knuh.rfid;

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
        System.out.println("Starting main!! ");     
        
		// 클래스 생성 초기화하
		RFIDLibrary r = RFIDLibrary.INSTANCE;

		// 리더기가 연결되어 있는지
		System.out.println("RFID READER DEVICE : " + r.ccr_device_find());
		
        while(r.ccr_device_find()) {
            try {
                String readStr = Reader();
                if(readStr != null){
                    System.out.println(readStr);
                    this.GoodBeep();
                    ReqService send = new ReqService();
                    log.info("rfid data : {}", readStr);
                    param.put("data", readStr);
                    send.tag(param);
                    try{
                        Thread.sleep(5000);
                    }catch(Exception e){System.out.println(e);}
                }
            } catch (Exception e) {
               System.out.println(e);
            }
        }
    }

    public void Writer(String pid) {
        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
        byte[] output = new byte[39];
        try {
            String hexString = byteArrayToHexString(pid.getBytes("UTF-8"));
            for(int i = 0; i < (32-hexString.length())/2 ; i++){
                hexString = hexString + "20";
            }
            String sendProtocol = new String("4301"+ hexString);
            rfid.ccr_data_transceive_ex(sendProtocol, output);
            this.GoodBeep();
        } catch (Exception e) {
            System.out.println("error : " + e);
            this.BadBeep();
        }
        try{
          
        }catch(Exception e){
            System.out.println("error : " + e);
            this.BadBeep();
        }

    }

    public String Reader() {
        RFIDLibrary rfid = RFIDLibrary.INSTANCE;
        String sendProtocol = new String("52012020202020202020202020202020202053");
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
            // 문자열 32비트 자르기
            for(int i = 4 ; i < 34 ; i++){
                sb.append(chars[i]);
            }
            byte[] bytes  = Hex.decodeHex(sb.toString().toCharArray());
            return new String(bytes,"utf-8");
        } catch (Exception e) {
           System.out.println(e);
           return null;
        }
    }
}
