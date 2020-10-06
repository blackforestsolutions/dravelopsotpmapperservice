package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class ZonedDateTimeServiceImpl implements ZonedDateTimeService {

    private final ZoneId zoneId;

    @Autowired
    public ZonedDateTimeServiceImpl(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public ZonedDateTime convertEpochMillisecondsToDate(long epochMilliseconds) {
        Instant instant = Instant.ofEpochMilli(epochMilliseconds);
        return ZonedDateTime.ofInstant(instant, zoneId);
    }
}
