package kr.co.jsol.acr122.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*

main method를 통해 카드 read, write 예제 확인.

 */

public class NFCReader {

    private static final Logger log = LoggerFactory.getLogger(NFCReader.class);

//    public static void main(String[] args) {
//        try {
//            // 카드 리더기를 포함하는 터미널을 검색
//            TerminalFactory factory = TerminalFactory.getDefault();
//            List<CardTerminal> terminals = factory.terminals().list();
//
//            if (terminals.isEmpty()) {
//                System.out.println("No card terminals available");
//                return;
//            } else {
//                System.out.println("Available terminals: " + terminals);
//            }
//
//            // ACS ACR122 인 터미널을 선택
//            CardTerminal terminal = terminals.get(0);
//            log.info("terminal name " + terminal.getName());
//
//            if(!terminal.getName().contains("ACS ACR122")){
//                log.info("This System support only ACS ACR122");
//            }
//
//            // 카드 리더기에 연결
//            Card card = terminal.connect("T=1");
//            log.info("card protocol " + card.getProtocol());
//            log.info("card atr " + card.getATR());
//            log.info("card basicChannel " + card.getBasicChannel());
//
//            // 카드에서 데이터를 읽음
//            CardChannel channel = card.getBasicChannel();
//            // i to hex value
//            ResponseAPDU response = channel.transmit(
//                    new CommandAPDU(
//                            new byte[]{
//                                    (byte) 0xFF,
//                                    (byte) 0xCA,
//                                    (byte) 0x00,
//                                    (byte) 0x00,
//                                    (byte) 0x00
//                            }));
//
//            // 읽은 데이터 출력
//            byte[] responseData = response.getData();
//            String data = new String(responseData, StandardCharsets.UTF_8);
//            System.out.println("Read data: " + data);
//
//            // 카드와의 연결 종료
//            card.disconnect(true);
//        } catch (CardException e) {
//            e.printStackTrace();
//        }
//    }
}
