package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ZonedDateTimeServiceTest {

    private final ZonedDateTimeService classUnderTest = new ZonedDateTimeServiceImpl(ZoneId.of("Europe/Berlin"));

    @Test
    void test_convertEpochMillisecondsToDate_with_milliseconds_returns_a_correctly_parsed_date() {
        long epochMilliseconds = 1601464093000L;

        ZonedDateTime result = classUnderTest.convertEpochMillisecondsToDate(epochMilliseconds);

        assertThat(result).isEqualTo(ZonedDateTime.parse("2020-09-30T13:08:13+02:00"));
    }
}
