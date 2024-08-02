package kr.co.jsol.jagency.common.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = "#!@#";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return (stringList != null) ? String.join(SPLIT_CHAR, stringList) : null;
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return (string != null) ? Arrays.stream(string.split(SPLIT_CHAR)).collect(Collectors.toList()) : Collections.emptyList();
    }

    public static String getSplitChar() {
        return SPLIT_CHAR;
    }
}
