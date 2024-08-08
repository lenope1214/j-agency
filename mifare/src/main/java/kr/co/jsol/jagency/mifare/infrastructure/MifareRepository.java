/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2015 Marc de Verdelhan
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package kr.co.jsol.jagency.mifare.infrastructure;

import kr.co.jsol.jagency.common.application.utils.HexUtils;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;
import kr.co.jsol.jagency.mifare.application.dto.WriteMifareDto;
import kr.co.jsol.jagency.mifare.application.utils.MifareUtils;
import kr.co.jsol.jagency.reader.infrastructure.Acr122Device;
import org.nfctools.mf.MfCardListener;
import org.nfctools.mf.card.MfCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static kr.co.jsol.jagency.mifare.application.RFIDTagAllocator.*;

/**
 * Entry point of the program.
 * <p>
 * Manager for an ACR122 reader/writer.
 */
@Component
public class MifareRepository {

    private boolean isReading = false;

    private static final Logger log = LoggerFactory.getLogger(MifareRepository.class);

    @Value("${mifare.debug:false}")
    Boolean debug;

    // 180302를 hex로 변환했을때 결과 313830333032 로 하려고 했으나 기존에 사용하던 태그들의 Key 값이 FFFFFFFFFFFF 라서
    // 키를 변경할 수 없었음.
    @Value("${mifare.secret-key:FFFFFFFFFFFF}")
    String secretKey;

    private List<String> getKeys() {
        final List<String> keys = new ArrayList<>();
        // secretKey + MifareUtils.COMMON_MIFARE_CLASSIC_1K_KEYS
        keys.add(secretKey);
        keys.addAll(MifareUtils.COMMON_MIFARE_CLASSIC_1K_KEYS);
        return keys;
    }

//    public static String endOfFile = "00";

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
     *
     * @param card a card
     */
    private void printCardInfo(MfCard card) {
        System.out.println("Card detected: "
                + card.getTagType().toString() + " "
                + card.toString() + " "
        );

        int sectors = card.getSectors();
        System.out.println("total sector size = " + sectors);
        for (int i = 3; i < sectors; i++) {
            System.out.println("Sector " + i + card.getBlocksPerSector(i));
        }
    }

    public boolean isConnected() {
        try {
            new Acr122Device();
            return true;
        } catch (Exception re) {
//            System.out.println("No ACR122 reader found.");
            // 현재시간 + ACR122 Reader Not Found
            log.info("[{}]ACR122 Reader Not Found", LocalDateTime.now());
            return false;
        }
    }

    /**
     * Listens for cards using the provided listener.
     *
     * @param listener a listener
     */
    private void listen(MfCardListener listener) throws IOException {
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
    public void read(Consumer<String> callback) throws IOException {
        if (isReading) {
            return;
        }
        List<String> keys = getKeys();
        MfCardListener listener = (mfCard, mfReaderWriter) -> {
            try {
                isReading = true;
                MifareUtils.dumpMifareClassic1KCard(mfReaderWriter, mfCard, keys, callback);
            } catch (CardException ce) {
                throw new GeneralClientException.BadRequestException("카드 정보를 읽을 수 없습니다.");
            } finally {
                isReading = false;
            }
        };

        // Start listening
        listen(listener);
    }


    /**
     * Writes to cards.
     *
     * @param writeMifareDto 쓰려는 카드의 Sector ID, Block ID, Key(Hex, 16진수), Data(Hex, 16진수) 값으로만 이루어져 있어야 한다.
     */
    public void writeToCards(WriteMifareDto writeMifareDto) throws IOException {
        List<String> keys = getKeys();
        if (debug) {
            log.info("writeAcr122Dto = {}", writeMifareDto);
            log.info("secretKey = {}", secretKey);
            log.info("keys = {}", keys);
//            log.info("endOfFile = {}", endOfFile);
        }
        final String inputData = writeMifareDto.getData();

        // 입력 종료 플래그(00) 추가, 추가하지 않으면 Block에 딱 맞게 입력된 경우 다음 블럭에 값이 있을 경우 이전에 입력한 값도 읽게 됨
        final String hexString = HexUtils.stringToHex(inputData) + "00";
//        final String hexString = HexUtils.stringToHex(inputData) + endOfFile; // 마지막 블록에는 EOF(Null)를 넣어준다.

        if (debug) {
            log.info("inputData = {}", inputData);
            log.info("hexString = {}", hexString);
        }

        // 총 1~15까지 15섹터, 각 섹터당 4개의 블록을 갖고, 각 블록당 16바이트를 갖는다.
        // hexData를 전부 넣을 수 있는지 확인하고 넣을 수 없다면 에러를 throw한다.
        int requiredBlocks = calculateRequiredBlocks(hexString);
        List<String> blocks = allocateBlocks(hexString);
        List<List<String>> sectors = allocateToSectors(blocks);
        if (debug) {
            log.info("Required blocks: {}", requiredBlocks);
            log.info("Blocks: {}", blocks);
            log.info("Sectors: {}", sectors);
        }

        for (int sectorNumber = 0; sectorNumber < sectors.size(); sectorNumber++) {
            int sectorId = sectorNumber + 1;
            for (int blockId = 0; blockId < sectors.get(sectorNumber).size(); blockId++) {
                if (debug) {
                    log.info("[{}-{}]: {}", sectorId, blockId, sectors.get(sectorNumber).get(blockId));
                }

                // 불가능한 데이터 입력을 받으면 도움말을 반환한다.
                for (int k = 0; k < keys.size(); k++) {
                    String key = keys.get(k);

                    if (!MifareUtils.isValidMifareClassic1KSectorIndex(sectorId)) {
                        throw new GeneralClientException.BadRequestException("Sector ID가 올바르지 않습니다.");
                    }

                    if (!MifareUtils.isValidMifareClassic1KBlockIndex(blockId)) {
                        throw new GeneralClientException.BadRequestException("Block ID가 올바르지 않습니다.");
                    }

                    if (!MifareUtils.isValidMifareClassic1KKey(secretKey)) {
                        throw new GeneralClientException.BadRequestException("Key가 올바르지 않습니다.");
                    }

                    if (!HexUtils.isHexString(hexString)) {
                        throw new GeneralClientException.BadRequestException("Hex 값이 올바르지 않습니다.");
                    }

                    // Card listener for writing
                    int finalSectorNumber = sectorNumber;
                    int finalBlockNumber = blockId;
                    MfCardListener listener = (mfCard, mfReaderWriter) -> {
                        try {
                            MifareUtils.writeToMifareClassic1KCard(mfReaderWriter,
                                    mfCard,
                                    sectorId,
                                    finalBlockNumber,
                                    key,
                                    sectors.get(finalSectorNumber).get(finalBlockNumber));
                        } catch (CardException ce) {
                            log.error("Card removed or not present.");
                        }
                    };

                    // Start listening
                    listen(listener);
                }
            }
        }

        return;
    }

//    public static void main(String... args) {
//        WriteAcr122Dto writeAcr122Dto = new WriteAcr122Dto() {{
//            setData("9");
//        }};
//
//        // 영어 = 1바이트
//        // 한글 = 2바이트
//        // 숫자 = 1바이트
//
//        // 블록 수를 계산하기 위해 데이터를 Hex로 변환했을때 나오는 길이를 미리 확인하여
//        // 15섹터 * 4블록 * 16바이트 이내로 들어가는지 확인한다. (15 * 4 * 16 = 960)
//
//        final String inputString = writeAcr122Dto.getData();
//        final String hexString = HexUtils.stringToHex(inputString);
//
//        int hexBytes = hexString.length() / 2;
//        System.out.println("Hex representation: " + hexString + " (" + hexBytes + " bytes)");
//
//    }

/**
 * Prints help and exits.
 */
//    public void printHelpAndExit() {
//        String jarPath = Acr122Repository.class.getProtectionDomain().getCodeSource().getLocation().getFile();
//        String jarName = jarPath.substring(jarPath.lastIndexOf('/') + 1);
//
//        StringBuilder sb = new StringBuilder("Usage: java -jar ");
//        sb.append(jarName).append(" [option]\n");
//
//        sb.append("Options:\n");
//        sb.append("\t-h, --help\t\t\tshow this help message and exit\n");
//        sb.append("\t-d, --dump [KEYS...]\t\tdump Mifare Classic 1K cards using KEYS\n");
//        sb.append("\t-w, --write S B KEY DATA\twrite DATA to sector S, block B of Mifare Classic 1K cards using KEY\n");
//
//        sb.append("Examples:\n");
//        sb.append("\tjava -jar ").append(jarName).append(" --dump FF00A1A0B000 FF00A1A0B001 FF00A1A0B099\n");
//        sb.append("\tjava -jar ").append(jarName).append(" --write 13 2 FF00A1A0B001 FFFFFFFFFFFF00000000060504030201");
//
//        System.out.println(sb.toString());
//
//        System.exit(0);
//    }
}
