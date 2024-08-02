/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Marc de Verdelhan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package kr.co.jsol.acr122.application;

import kr.co.jsol.acr122.application.dto.WriteAcr112Dto;
import kr.co.jsol.jagency.common.application.utils.HexUtils;
import kr.co.jsol.acr122.application.utils.MifareUtils;
import kr.co.jsol.jagency.common.application.utils.StringUtils;
import kr.co.jsol.jagency.common.infrastructure.exception.CustomException;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;
import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.card.MfCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.util.Collections;

/**
 * Entry point of the program.
 * <p>
 * Manager for an ACR122 reader/writer.
 */
@Component
public class Acr122Manager {

    private static final Logger log = LoggerFactory.getLogger(Acr122Manager.class);

    @Value("${acr122.secret-key:313830333032}") // 180302를 hex로 변환했을때 결과
    String secretKey;

//    /**
//     * Entry point.
//     * @param args the command line arguments
//     * @see Acr122Manager#printHelpAndExit()
//     */
//    public  void main(String[] args) throws IOException {
//        if (args == null || args.length == 0) {
//            printHelpAndExit();
//        }
//
//        switch (args[0]) {
//            case "-d":
//            case "--dump":
//                log.info("DUMP(READ) 진행");
//                dumpCards(args);
//                break;
//            case "-w":
//            case "--write":
//                log.info("WRITE 진행");
//                writeToCards(args);
//                break;
//            case "-h":
//            case "--help":
//            default:
//                printHelpAndExit();
//                break;
//        }
//    }

    /**
     * Prints information about a card.
     * @param card a card
     */
    private  void printCardInfo(MfCard card) {
        System.out.println("Card detected: "
                + card.getTagType().toString() + " "
                + card.toString()+ " "
        );

        int sectors = card.getSectors();
        System.out.println("total sector size = " + sectors);
        for (int i = 3; i < sectors; i++) {
            System.out.println("Sector " + i + card.getBlocksPerSector(i));
        }
    }

    public   boolean isConnected(){
        try {
            new Acr122Device();
            return true;
        } catch (Exception re) {
            System.out.println("No ACR122 reader found.");
            return false;
        }
    }

    /**
     * Listens for cards using the provided listener.
     * @param listener a listener
     */
    private  void listen(MfCardListener listener) throws IOException {
        Acr122Device acr122;
        try {
            acr122 = new Acr122Device();
        } catch (RuntimeException re) {
            System.out.println("No ACR122 reader found.");
            return;
        }
        acr122.open();
        acr122.listen(listener);
    }

    /**
     * read blocks.
     */
    public void read() throws IOException {
        // Card listener for dump

        MfCardListener listener = (mfCard, mfReaderWriter) -> {
            printCardInfo(mfCard);
            try {
                MifareUtils.dumpMifareClassic1KCard(mfReaderWriter, mfCard, Collections.singletonList(secretKey));
            } catch (CardException ce) {
                throw new GeneralClientException.BadRequestException("카드 정보를 읽을 수 없습니다.");
            }
        };

        // Start listening
        listen(listener);
    }

    /**
     * Writes to cards.
     * @param writeAcr112Dto 쓰려는 카드의 Sector ID, Block ID, Key(Hex, 16진수), Data(Hex, 16진수) 값으로만 이루어져 있어야 한다.
     */
    public  void writeToCards(WriteAcr112Dto writeAcr112Dto) throws IOException {
        log.info("writeAcr112Dto = {}", writeAcr112Dto);
        final Integer sectorId = writeAcr112Dto.getSectorId();
        final Integer blockId = writeAcr112Dto.getBlockId();
        final String key = writeAcr112Dto.getKey().toUpperCase();
        final String strData = writeAcr112Dto.getData();
        final String hexData = HexUtils.stringToHex(strData);

        // 불가능한 데이터 입력을 받으면 도움말을 반환한다.
        if (
                !MifareUtils.isValidMifareClassic1KSectorIndex(sectorId)
                || !MifareUtils.isValidMifareClassic1KBlockIndex(blockId)
                || !MifareUtils.isValidMifareClassic1KKey(key)
                || !HexUtils.isHexString(hexData)
        ) {
            printHelpAndExit();
        }

        // Card listener for writing
        MfCardListener listener = (mfCard, mfReaderWriter) -> {
            printCardInfo(mfCard);
            try {
                MifareUtils.writeToMifareClassic1KCard(mfReaderWriter, mfCard, sectorId, blockId, key, hexData);
            } catch (CardException ce) {
                System.out.println("Card removed or not present.");
            }
        };

        // Start listening
        listen(listener);
    }

    /**
     * Prints help and exits.
     */
    public  void printHelpAndExit() {
        String jarPath = Acr122Manager.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        String jarName = jarPath.substring(jarPath.lastIndexOf('/') + 1);

        StringBuilder sb = new StringBuilder("Usage: java -jar ");
        sb.append(jarName).append(" [option]\n");

        sb.append("Options:\n");
        sb.append("\t-h, --help\t\t\tshow this help message and exit\n");
        sb.append("\t-d, --dump [KEYS...]\t\tdump Mifare Classic 1K cards using KEYS\n");
        sb.append("\t-w, --write S B KEY DATA\twrite DATA to sector S, block B of Mifare Classic 1K cards using KEY\n");

        sb.append("Examples:\n");
        sb.append("\tjava -jar ").append(jarName).append(" --dump FF00A1A0B000 FF00A1A0B001 FF00A1A0B099\n");
        sb.append("\tjava -jar ").append(jarName).append(" --write 13 2 FF00A1A0B001 FFFFFFFFFFFF00000000060504030201");

        System.out.println(sb.toString());

        System.exit(0);
    }
}
