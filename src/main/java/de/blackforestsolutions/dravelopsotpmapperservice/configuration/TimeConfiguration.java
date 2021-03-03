package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

@Configuration
public class TimeConfiguration {

    @Value("${otp.timeZone}")
    private String timeZone;


    @Bean
    public ZoneId zoneId() {
        return ZoneId.of(timeZone);
    }
}
