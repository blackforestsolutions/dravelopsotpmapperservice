package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.ZoneId;
import java.util.Locale;

@SpringBootConfiguration
public class OpenTripPlannerConfiguration {

    @Value("${otp.protocol}")
    private String protocol;
    @Value("${otp.host}")
    private String host;
    @Value("${otp.port}")
    private int port;
    @Value("${otp.router}")
    private String router;
    @Value("${otp.timeZone}")
    private String timeZone;

    @Bean(name = "openTripPlannerApiToken")
    public ApiToken apiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setRouter(router)
                .build();
    }

    @Bean
    public ZoneId zoneId() {
        return ZoneId.of(timeZone);
    }
}
