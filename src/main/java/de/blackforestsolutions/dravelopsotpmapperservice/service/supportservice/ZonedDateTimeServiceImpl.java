package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RefreshScope
@Service
public class ZonedDateTimeServiceImpl implements ZonedDateTimeService {

    @Value("${otp.timeZone}")
    private String timeZone;

    @Override
    public ZonedDateTime convertEpochMillisecondsToDate(long epochMilliseconds) {
        Instant instant = Instant.ofEpochMilli(epochMilliseconds);
        return ZonedDateTime.ofInstant(instant, ZoneId.of(timeZone));
    }
}
