package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ApiServiceTestConfiguration.class)
@TestConfiguration
public class OpenTripPlannerTestConfiguration {

    @Value("${otp.maxResults}")
    private Integer nearestStationsMaxResults;
    @Value("${otpmapper.maxResults}")
    private Integer journeyMaxResults;

    @Bean
    @ConfigurationProperties(prefix = "otp.apitokens[0]")
    public ApiToken.ApiTokenBuilder journeyOtpApiToken(@Autowired ApiToken journeyOtpMapperApiToken) {
        return new ApiToken.ApiTokenBuilder(journeyOtpMapperApiToken)
                .setMaxResults(journeyMaxResults);
    }

    @Bean
    @ConfigurationProperties(prefix = "otp.apitokens[0]")
    public ApiToken.ApiTokenBuilder nearestStationsOtpApiToken(@Autowired ApiToken nearestStationsOtpMapperApiToken) {
        return new ApiToken.ApiTokenBuilder(nearestStationsOtpMapperApiToken)
                .setMaxResults(nearestStationsMaxResults);
    }
}