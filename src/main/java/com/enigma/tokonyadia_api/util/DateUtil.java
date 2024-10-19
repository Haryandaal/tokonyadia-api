package com.enigma.tokonyadia_api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final String PATTERN_LOCAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_LOCAL_DATE_TIME);
        return dateTimeFormatter.format(localDateTime);
    }
}
