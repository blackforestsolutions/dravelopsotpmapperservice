package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${graphql.playground.tabs.JOURNEY_QUERY.departurePlaceholder}")
    private String departure;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.arrivalPlaceholder}")
    private String arrival;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.maxResults}")
    private Integer maxResults;
    @Value("${graphql.playground.tabs.NEAREST_ADDRESSES.layers}")
    private String[] layers;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasReverseApiToken() {
        ApiToken apiToken = new ApiToken();
        apiToken.setDeparture(departure);
        apiToken.setArrival(arrival);
        apiToken.setLanguage(language);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(Arrays.asList(layers));
        return apiToken;
    }

    @Bean
    public Point peliasPoint() {
        return new Point.PointBuilder(longitude, latitude)
                .build();
    }
}