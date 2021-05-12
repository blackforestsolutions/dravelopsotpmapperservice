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

    @Value("${graphql.playground.tabs.NEAREST_STATIONS.maxResults}")
    private Integer nearestStationsMaxResults;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.maxResults}")
    private Integer journeyMaxResults;

    @Bean
    @ConfigurationProperties(prefix = "otp.apitokens[0]")
    public ApiToken journeyOtpApiToken(@Autowired ApiToken journeyOtpMapperApiToken) {
        ApiToken apiToken = new ApiToken(journeyOtpMapperApiToken);
        apiToken.setMaxResults(journeyMaxResults);
        return apiToken;
    }

    @Bean
    @ConfigurationProperties(prefix = "otp.apitokens[0]")
    public ApiToken nearestStationsOtpApiToken(@Autowired ApiToken nearestStationsOtpMapperApiToken) {
        ApiToken apiToken = new ApiToken(nearestStationsOtpMapperApiToken);
        apiToken.setMaxResults(nearestStationsMaxResults);
        return apiToken;
    }
}