package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${graphql.playground.tabs[0].departurePlaceholder}")
    private String departure;
    @Value("${graphql.playground.tabs[0].arrivalPlaceholder}")
    private String arrival;
    @Value("${graphql.playground.tabs[3].variables.language}")
    private Locale language;
    @Value("${graphql.playground.tabs[3].variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs[3].variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs[3].maxResults}")
    private Integer maxResults;
    @Value("${graphql.playground.tabs[3].layers}")
    private List<String> layers;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken peliasReverseApiToken() {
        ApiToken apiToken = new ApiToken();
        apiToken.setDeparture(departure);
        apiToken.setArrival(arrival);
        apiToken.setLanguage(language);
        apiToken.setMaxResults(maxResults);
        apiToken.setLayers(layers);
        return apiToken;
    }

    @Bean
    public Point peliasPoint() {
        return new Point.PointBuilder(longitude, latitude)
                .build();
    }
}