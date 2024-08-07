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
package kr.co.jsol.jagency.common.application.utils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Hexadecimal utility class.
 */
public final class HexUtils {

    /**
     * Regex pattern for hexadecimal strings
     */
    private static final Pattern HEX_STRING_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2})+$");

    /**
     * Array of all hexadecimal chars
     */
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    private HexUtils() {
    }

    /**
     * @param s a string
     * @return true if the provided string is hexadecimal, false otherwise
     */
    public static boolean isHexString(String s) {
        return (s != null) && HEX_STRING_PATTERN.matcher(s).matches();
    }

    /**
     * String To Hex String
     *
     * @param str
     * @return hex string
     */
    public static String stringToHex(String str) {
        StringBuilder hexString = new StringBuilder();
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }


    /**
     * @param hexString a hex string
     * @return a byte array
     */
    public static byte[] hexStringToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * @param bytes a byte array
     * @return a hex string
     */
    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String hexStringToUTF8String(String hexString) {
        // Hex 문자열을 byte 배열로 변환
        byte[] bytes = hexStringToBytes(hexString);

        // UTF-8 문자열로 변환
        return new String(bytes, StandardCharsets.UTF_8);
    }

    //    public static void main(String[] args) {
//        String hexString = "ec9588eb8595ed9598ec84b8ec9a943f20ec9db4eba087eab28c20eab8b420eab880ec9e90eb8f8420ec9e85eba0a520eab080eb8aa5ed95a0eab98cec9a943f"; // "안녕"의 UTF-8 인코딩
//        String result = hexStringToUTF8String(hexString);
//        System.out.println(result); // 출력: 안녕
//    }
}
