package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import java.time.ZonedDateTime;

public interface ZonedDateTimeService {
    ZonedDateTime convertEpochMillisecondsToDate(long epochMilliseconds);
}
