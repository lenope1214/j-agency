package kr.co.jsol.jagency.mifare.application;

import kr.co.jsol.jagency.common.application.utils.HexUtils;
import kr.co.jsol.jagency.common.infrastructure.exception.GeneralClientException;

import java.util.List;

public class RFIDTagAllocatorObject {

    private static final int MAX_BYTES = 950;

    private String inputString;
    private String hexString;
    private int requiredBlocks;
    private List<String> blocks;
    private List<List<String>> sectors;

    public RFIDTagAllocatorObject(String inputString) throws Exception {
        this.inputString = inputString;
        this.hexString = HexUtils.stringToHex(inputString);

        // Check if the byte size exceeds the limit
        checkHexStringLength(hexString);

        this.requiredBlocks = calculateRequiredBlocks(hexString);
        this.blocks = allocateBlocks(hexString);
        this.sectors = allocateToSectors(blocks);
    }

    private void checkHexStringLength(String hexString) {
        int hexBytes = hexString.length() / 2;
        if (hexBytes > MAX_BYTES) {
            throw new GeneralClientException.BadRequestException("Input string is too large");
        }
    }

    private int calculateRequiredBlocks(String hexString) {
        return RFIDTagAllocator.calculateRequiredBlocks(hexString);
    }

    private List<String> allocateBlocks(String hexString) {
        return RFIDTagAllocator.allocateBlocks(hexString);
    }

    private List<List<String>> allocateToSectors(List<String> blocks) {
        return RFIDTagAllocator.allocateToSectors(blocks);
    }

    public String getCombinedHexString() {
        return RFIDTagAllocator.combineSectors(sectors);
    }

    public String getOriginalString() {
        return HexUtils.hexStringToUTF8String(getCombinedHexString());
    }

    public String getInputString() {
        return inputString;
    }

    public String getHexString() {
        return hexString;
    }

    public int getRequiredBlocks() {
        return requiredBlocks;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public List<List<String>> getSectors() {
        return sectors;
    }
}
