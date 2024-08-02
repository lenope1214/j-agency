package kr.co.jsol.jagency.common.application.utils;

import java.util.UUID;

public class StringUtils {

    /**
     * String pascal case to camel case
     * ex : PascalCase -> pascalCase
     */
    public static String pascalToCamel(String str) {
        if (str != null && !str.isEmpty()) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        } else {
            return str;
        }
    }

    /**
     * String?.isNullOrBlank().not() function
     */
    public static boolean isNotNullOrBlank(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * if (createProductDto.qrBase.isNullOrBlank()) {
     * UUID.randomUUID().toString()
     * } else {
     * createProductDto.qrBase
     * },
     * 위와 같은 형식을 infix function 으로 사용할 수 있도록 함
     *
     * @example
     */
    public static String orRandomUUID(String str) {
        if (str == null || str.isEmpty()) {
            return UUID.randomUUID().toString();
        } else {
            return str;
        }
    }
}
