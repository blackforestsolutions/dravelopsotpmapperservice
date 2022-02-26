package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@RefreshScope
@Component
@ConfigurationProperties(prefix = "gtfs")
public class GtfsApiTokenConfiguration {

    private Map<String, Object> apitokens;
}
