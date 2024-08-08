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
package kr.co.jsol.jagency.mifare.application.utils;

import kr.co.jsol.jagency.common.application.utils.HexUtils;
import kr.co.jsol.jagency.common.infrastructure.exception.CustomException;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;
import kr.co.jsol.jagency.common.infrastructure.exception.domain.MifareClassic1KException;
import org.nfctools.mf.MfAccess;
import org.nfctools.mf.MfException;
import org.nfctools.mf.MfReaderWriter;
import org.nfctools.mf.block.BlockResolver;
import org.nfctools.mf.block.MfBlock;
import org.nfctools.mf.card.MfCard;
import org.nfctools.mf.classic.Key;
import org.nfctools.mf.classic.MemoryLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static kr.co.jsol.jagency.common.application.utils.HexUtils.*;


/**
 * Mifare utility class.
 */
public final class MifareUtils {

    /**
     * Mifare Classic 1K sector count
     */
    public static final int MIFARE_1K_SECTOR_COUNT = 16;

    /**
     * Mifare Classic 1K block count (per sector)
     */
    public static final int MIFARE_1K_PER_SECTOR_BLOCK_COUNT = 4;

    /**
     * Common Mifare Classic 1K keys
     */
    public static final List<String> COMMON_MIFARE_CLASSIC_1K_KEYS = Arrays.asList(
            "FFFFFFFFFFFF",
            "000000000000"
    );

    private static final Logger log = LoggerFactory.getLogger(MifareUtils.class);

    private MifareUtils() {
    }

    /**
     * @param s a string
     * @return true if the provided string is a valid Mifare Classic 1K key, false otherwise
     */
    public static boolean isValidMifareClassic1KKey(String s) {
        return isHexString(s) && (s.length() == 12);
    }

    /**
     * @param sectorIndex a sector index
     * @return true if the provided string is a valid Mifare Classic 1K sector index, false otherwise
     */
    public static boolean isValidMifareClassic1KSectorIndex(int sectorIndex) {
        try {
            return sectorIndex >= 0 && sectorIndex < MIFARE_1K_SECTOR_COUNT;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * @param blockIndex a block index
     * @return true if the provided string is a valid Mifare Classic 1K block index, false otherwise
     */
    public static boolean isValidMifareClassic1KBlockIndex(int blockIndex) {
        try {
            return blockIndex >= 0 && blockIndex < MIFARE_1K_PER_SECTOR_BLOCK_COUNT;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Dumps a Mifare Classic 1K card.
     *
     * @param reader the reader
     * @param card   the card
     * @param keys   the keys to be tested for reading
     */
    public static void dumpMifareClassic1KCard(MfReaderWriter reader, MfCard card, List<String> keys, Consumer<String> callback)
            throws CardException, CustomException {

        StringBuilder sb = new StringBuilder();
        boolean breakFlag = false;
        int endFlag = 1;

        for (int sectorNumber = 0; sectorNumber < MIFARE_1K_SECTOR_COUNT - 1 && !breakFlag; sectorNumber++) {
            int sectorIndex = sectorNumber + 1;
            for (int blockIndex = 0; blockIndex < MIFARE_1K_PER_SECTOR_BLOCK_COUNT - 1 && !breakFlag; blockIndex++) {
                // 섹터와 상관없이 0~2을 설정한다.
                // 3=  trail block이어서 읽을 수 없음.
                try {
                    String sectorRaw = dumpMifareClassic1KBlock(reader, card, sectorIndex, blockIndex, keys);
                    log.info("sectorRaw : {}", sectorRaw);

                    // 빈 블럭일 경우 결과에 아예 추가하지 않아야 한다.
                    if (sectorRaw.equals("00000000000000000000000000000000")) {
                        breakFlag = true;
                        break;
                    }

                    // 데이터를 추가한다.
                    sb.append(sectorRaw);

                    // 후처리 진행
                    // 만약 데이터가 존재하고 00으로 종료된다면 다음 블럭을 읽지 말아야 한다.
                    if (sectorRaw.endsWith("00")) {
                        endFlag = 2;
                        breakFlag = true;
                        break;
                    }

                } catch (MifareClassic1KException.NotReadableTrailBlock ignore) {
                    // 3번 블럭인 trailer block을 읽게됐을 경우 다음 섹터로 넘어가기 위해 continue
                    continue;
                } catch (CustomException e) {
                    // 읽을 수 없는 태그일 경우, 종료
                    log.error(e.getMessage());
                    breakFlag = true;
                } finally {
                    log.info("[읽기 종료] 모든 데이터를 확인하였습니다. 읽기를 종료합니다. [{}]", endFlag);
                }
            }
        }

        String hexString = sb.toString();
        String rawString = HexUtils.hexStringToUTF8String(hexString).trim();
        log.info("데이터 읽기 결과 : {} ({})", rawString, hexString);

        if (callback != null) {
            callback.accept(rawString);
        }
    }

    /**
     * Write data to a Mifare Classic 1K card.
     *
     * @param reader     the reader
     * @param card       the card
     * @param sectorId   the sector to be written
     * @param blockId    the block to be written
     * @param key        the key to be used for writing
     * @param dataString the data hex string to be written
     */
    public static void writeToMifareClassic1KCard(MfReaderWriter reader, MfCard card, int sectorId,
                                                  int blockId, String key, String dataString)
            throws CardException {
        if (!isValidMifareClassic1KKey(key)) {
            log.info("The key {} is not valid.", key);
            return;
        }
        if (!isHexString(dataString)) {
            log.info("{} is not an hex string.", dataString);
            return;
        }

        byte[] keyBytes = hexStringToBytes(key);
        // Reading with key A
        MfAccess access = new MfAccess(card, sectorId, blockId, Key.A, keyBytes);
        String blockData = readMifareClassic1KBlock(reader, access);
        if (blockData == null) {
            // Reading with key B
            access = new MfAccess(card, sectorId, blockId, Key.B, keyBytes);
            blockData = readMifareClassic1KBlock(reader, access);
        }
        log.info("[{}-{}-{}-{}]blockData : {}", key, sectorId, access.getKey(), blockId, blockData);
        if (blockData != null) {
            // Block read
//            log.info("[읽기성공]");
//            log.info("sectorId : {}", sectorId);
//            log.info("blockId : {}", blockId);
//            log.info("key : {}", key);
//            log.info("dataString : {}", dataString);
//            log.info("blockData : {}", blockData);
//            log.info("[데이터 출력 완료]");

            // Writing with same key
            boolean written = false;
            try {
                // 데이터를 입력하기 위해 Hex 문자열을 byte[]로 변환
                byte[] data = hexStringToBytes(dataString);
                log.info("입력받은 hex값({})을 Bytes로 변경한 결과 : {}", dataString, Arrays.toString(data));
                MfBlock block = BlockResolver.resolveBlock(MemoryLayout.CLASSIC_1K, sectorId, blockId, data);
//                log.info("block : " + block);
                written = writeMifareClassic1KBlock(reader, access, block);
            } catch (MfException me) {
                log.info("[쓰기 에러 발생]: {}", me.getMessage());
            }


            if (written) {
                blockData = readMifareClassic1KBlock(reader, access);
                if (blockData != null) {
                    log.info("[쓰기 성공] sectorId: {}, blockId: {}, key: {}", sectorId, blockId, key);
                    log.info("New block data: {}, Key {}: {}", blockData, access.getKey(), key);

//                    // hexString to utf8
//                    String result = HexUtils.hexStringToUTF8String(blockData);
//                    log.info("[UTF8] " + result);
                }
            }
        }
    }

    /**
     * Reads a Mifare Classic 1K block.
     *
     * @param reader the reader
     * @param access the access
     * @return a string representation of the block data, null if the block can't be read
     */
    private static String readMifareClassic1KBlock(MfReaderWriter reader, MfAccess access)
            throws CardException {
        String data = null;
        try {
            MfBlock block = reader.readBlock(access)[0];
            data = bytesToHexString(block.getData());
        } catch (IOException ioe) {
            if (ioe.getCause() instanceof CardException) {
                throw (CardException) ioe.getCause();
            }
        }
        return data;
    }

    /**
     * Writes a Mifare Classic 1K block.
     *
     * @param reader the reader
     * @param access the access
     * @param block  the block to be written
     * @return true if the block has been written, false otherwise
     */
    private static boolean writeMifareClassic1KBlock(MfReaderWriter reader, MfAccess access, MfBlock block) throws
            CardException {
        boolean written = false;
        try {
            reader.writeBlock(access, block);
            written = true;
        } catch (IOException ioe) {
            if (ioe.getCause() instanceof CardException) {
                throw (CardException) ioe.getCause();
            }
        }
        return written;
    }

    /**
     * Dumps Mifare Classic 1K block data.
     *
     * @param reader   the reader
     * @param card     the card
     * @param sectorId the sector to be read
     * @param blockId  the block to be read
     * @param keys     the keys to be tested for reading
     * @return a string representation of the block data, hex data
     */
    //TODO 복수 keys에서 단일 key로 변경해서 요청자가 key를 단일로 입력하고 하나라도 실패하면 그 다음엔 같은 key를 사용하지 않도록 해
    // 데이터 조회 성능 향상이 필요해 보임.
    private static String dumpMifareClassic1KBlock(
            MfReaderWriter reader,
            MfCard card,
            int sectorId,
            int blockId,
            List<String> keys
    ) throws CardException, CustomException {
        if (sectorId < 1 || sectorId > 15) {
            throw new GeneralClientException.BadRequestException("읽을 수 없는 태그입니다. [sectorId must be 1~15]");
        }
        if (blockId < 0 || blockId > 3) {
            throw new GeneralClientException.BadRequestException("읽을 수 없는 태그입니다. [blockId must be 0~3]");
        }
        if (blockId == 3) {
            throw new MifareClassic1KException.NotReadableTrailBlock();
        }

        for (String key : keys) {

            log.info("key: {}, sector: {}, block: {}", key, sectorId, blockId);
            // For each provided key...
            if (isValidMifareClassic1KKey(key)) {
                byte[] keyBytes = hexStringToBytes(key);
                // Reading with key A
                MfAccess access = new MfAccess(card, sectorId, blockId, Key.A, keyBytes);
                String blockData = readMifareClassic1KBlock(reader, access);
//                log.info("A로 읽었을 때 결과 : {}", blockData);

                // A키로 데이터를 읽지 못 했다면, B키로 재시도한다.
                if (blockData == null) {
                    // Reading with key B
                    access = new MfAccess(card, sectorId, blockId, Key.B, keyBytes);
                    blockData = readMifareClassic1KBlock(reader, access);
                }

//                log.info("B로 읽었을 때 결과 : {}", blockData);

                if (blockData != null) {
                    log.info("{} (Key {}: {})", blockData, access.getKey(), key);
                    return blockData;
                }

            }
        }

        // 모든 secretKeys, key(A,B)로 읽지 못 했다면 못 읽는 RFID 태그임.
//        log.info("<Failed> keys: {}, sectorId: {}, blockId: {}", keys, sectorId, blockId);
        throw new GeneralClientException.BadRequestException("읽을 수 없는 태그입니다.");
    }
}


