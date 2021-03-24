package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ZonedDateTimeServiceTest {

    private final ZonedDateTimeService classUnderTest = new ZonedDateTimeServiceImpl();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(classUnderTest, "timeZone", "Europe/Berlin");
    }

    @Test
    void test_convertEpochMillisecondsToDate_with_summertime_timestamp_returns_the_correct_time_offset_and_zoneId() {
        long epochMilliseconds = 1601464093000L;

        ZonedDateTime result = classUnderTest.convertEpochMillisecondsToDate(epochMilliseconds);

        assertThat(result).isEqualTo(ZonedDateTime.parse("2020-09-30T13:08:13+02:00[Europe/Berlin]"));
    }

    @Test
    void test_convertEpochMillisecondsToDate_with_wintertime_timestamp_returns_the_correct_time_offset_and_zoneId() {
        long epochMilliseconds = 1607155745000L;

        ZonedDateTime result = classUnderTest.convertEpochMillisecondsToDate(epochMilliseconds);

        assertThat(result).isEqualTo(ZonedDateTime.parse("2020-12-05T09:09:05+01:00[Europe/Berlin]"));
    }

}
