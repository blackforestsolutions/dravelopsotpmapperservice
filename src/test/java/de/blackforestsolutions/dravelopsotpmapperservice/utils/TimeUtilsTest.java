package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilsTest {

    @Test
    void test_convertEpochMillisecondsToDate_with_milliseconds_returns_a_correctly_parsed_date() {
        long epochMilliseconds = 1601464093000L;

        LocalDateTime result = TimeUtils.convertEpochMillisecondsToDate(epochMilliseconds);

        assertThat(result).isEqualTo(LocalDateTime.parse("2020-09-30T13:08:13"));
    }
}
