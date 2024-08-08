package kr.co.jsol.jagency.mifare.application.utils;

public class SplitString {
    public static void main(String[] args) {
        String sectorRaw = "abcdefg";
        String[] split = sectorRaw.split("(?<=\\G.{2})");

        // 결과 출력
        for (String part : split) {
            System.out.println(part);
        }
    }
}
