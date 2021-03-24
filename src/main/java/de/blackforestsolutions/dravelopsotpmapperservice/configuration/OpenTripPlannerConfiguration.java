package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@RefreshScope
@Component
@ConfigurationProperties(prefix = "otp")
public class OpenTripPlannerConfiguration {

    private List<ApiToken> apiTokens;
    @Value("${graphql.playground.tabs[4].maxResults}")
    private Integer maxResults;

    @Setter
    @Getter
    public static class ApiToken {

        private String protocol;
        private String host;
        private Integer port;
        private String router;
    }
}
