package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "otp")
public class OpenTripPlannerConfiguration {

    private List<ApiToken> apiTokens;

    @Setter
    @Getter
    public static class ApiToken {

        private String protocol;
        private String host;
        private Integer port;
        private String router;
        private Boolean showIntermediateStops;
        private Integer journeySearchWindowInMinutes;

    }
}
