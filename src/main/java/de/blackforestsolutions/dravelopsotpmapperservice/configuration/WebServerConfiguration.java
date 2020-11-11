package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootConfiguration
public class WebServerConfiguration {

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return new DravelOpsJsonMapper();
    }
}
