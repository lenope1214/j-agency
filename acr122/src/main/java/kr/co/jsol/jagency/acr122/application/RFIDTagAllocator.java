package kr.co.jsol.jagency.acr122.application;

import kr.co.jsol.jagency.common.application.utils.StringUtils;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;

import java.util.ArrayList;
import java.util.List;

public class RFIDTagAllocator {
    private static final int BLOCK_SIZE = 32; // 16 * 2 (각 자리당 4비트여서 총 2자리)
    private static final int MAX_BYTES = 950; // Maximum allowable size in bytes

    public static int calculateRequiredBlocks(String hexString) {
        int requiredBlocks = hexString.length() / BLOCK_SIZE;
        if (hexString.length() % BLOCK_SIZE != 0) {
            requiredBlocks += 1;
        }
        return requiredBlocks;
    }


    public static List<String> allocateBlocks(String hexString) {
        List<String> blocks = new ArrayList<>();
        for (int i = 0; i < hexString.length(); i += BLOCK_SIZE) {
            // uppercase를 안 해도 되지만 데이터를 확인할때 용이함
            blocks.add(hexString.substring(i, Math.min(i + BLOCK_SIZE, hexString.length())).toUpperCase());

            // 만약 해당 block이 BLOCK_SIZE만큼의 자리가 안되면 나머지를 Null로 채워준다.
            if (blocks.get(blocks.size() - 1).length() < BLOCK_SIZE) {
                blocks.set(
                        blocks.size() - 1, // 마지막 입력된 값을,
                        blocks.get(blocks.size() - 1) + // 기존 입력 값 + // 예로 31 만 입력돼있다면
                                // 31000000000000000000000000000000
                                StringUtils.repeat("0", BLOCK_SIZE - blocks.get(blocks.size() - 1).length()) // 0을 추가
                );
            }
        }
        return blocks;
    }

    // allocateBlocks 테스트하는 main method
//    public static void main(String[] args) {
//        String inputString = "안녕하세요? 이렇게 긴 글자도 입력 가능할까요?";
//        String hexString = HexUtils.stringToHex(inputString); // Use this line instead of the above line
//
//        checkHexStringLength(hexString);
//
//        int requiredBlocks = calculateRequiredBlocks(hexString);
//        System.out.println("Required blocks: " + requiredBlocks);
//
//        List<String> blocks = allocateBlocks(hexString);
//        System.out.println("Blocks: " + blocks);
//    }

    public static List<List<String>> allocateToSectors(List<String> blocks) {
        int sectorSize = 3; // 4번은 trailer block이므로 3개의 block이 sector를 이룸
        List<List<String>> sectors = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i += sectorSize) {
            sectors.add(blocks.subList(i, Math.min(i + sectorSize, blocks.size())));
        }
        return sectors;
    }

    public static String combineSectors(List<List<String>> sectors) {
        StringBuilder combinedHexString = new StringBuilder();
        for (List<String> sector : sectors) {
            for (String block : sector) {
                combinedHexString.append(block);
            }
        }
        return combinedHexString.toString();
    }

    public static void checkHexStringLength(String hexString) {
        int hexBytes = hexString.length() / 2;
        System.out.println("Hex representation: " + hexString + " (" + hexBytes + " bytes)");
        if (hexBytes > MAX_BYTES) {
            throw new GeneralClientException.BadRequestException("Input string is too large");
        }
    }

//    public static void main(String[] args) {
////        String inputString = StringUtils.repeat("예", 320);  // Replace with your input string
//        String inputString = "예시 문자열입니다123";
//        String hexString = HexUtils.stringToHex(inputString); // Use this line instead of the above line
//
//        checkHexStringLength(hexString);
//
//        int requiredBlocks = calculateRequiredBlocks(hexString);
//        System.out.println("Required blocks: " + requiredBlocks);
//
//        List<String> blocks = allocateBlocks(hexString);
//        System.out.println("Blocks: " + blocks);
//
//        List<List<String>> sectors = allocateToSectors(blocks);
//        System.out.println("Sectors: " + sectors);
//
//        for (int i = 0; i < sectors.size(); i++) {
//            System.out.println("Sector " + i + ": " + sectors.get(i));
//        }
//
//        String combinedHexString = combineSectors(sectors);
//        System.out.println("Combined Hex String: " + combinedHexString);
//
////        String originalString = hexToString(combinedHexString);
//        String originalString = HexUtils.hexStringToUTF8String(combinedHexString); // Use this line instead of the above line
//        System.out.println("Original String: " + originalString);
//    }
}
