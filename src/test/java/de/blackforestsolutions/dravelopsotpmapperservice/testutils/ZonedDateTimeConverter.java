package de.blackforestsolutions.dravelopsotpmapperservice.testutils;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;

public class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(String date) {
        return ZonedDateTime.parse(date);
    }
}
