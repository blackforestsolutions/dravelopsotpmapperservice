package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class TimeUtils {

    public static LocalDateTime convertEpochMillisecondsToDate(long epochMilliseconds) {
        Instant instant = Instant.ofEpochMilli(epochMilliseconds);
        return LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
    }
}
