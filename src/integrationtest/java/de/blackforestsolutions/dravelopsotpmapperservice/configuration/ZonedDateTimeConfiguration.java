package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.converter.ZonedDateTimeConverter;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ZonedDateTimeConfiguration {

    @Bean
    @ConfigurationPropertiesBinding
    public ZonedDateTimeConverter zonedDateTimeConverter() {
        return new ZonedDateTimeConverter();
    }
}
