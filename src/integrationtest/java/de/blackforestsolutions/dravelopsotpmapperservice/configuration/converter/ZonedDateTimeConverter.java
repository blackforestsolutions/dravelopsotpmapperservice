package de.blackforestsolutions.dravelopsotpmapperservice.configuration.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ConfigurationPropertiesBinding
public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

    @Value("${otp.timeZone}")
    private String timeZone;

    @Override
    public ZonedDateTime convert(@NonNull String time) {
        return LocalDate.now()
                .atTime(LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME))
                .atZone(ZoneId.of(timeZone))
                .plusDays(1L);
    }
}