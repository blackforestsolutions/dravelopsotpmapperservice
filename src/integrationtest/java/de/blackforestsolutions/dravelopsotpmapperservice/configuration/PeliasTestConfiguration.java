package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

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

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasReverseApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setArrival(arrival)
                .setLanguage(language);
    }

    @Bean
    public Point peliasPoint() {
        return new Point.PointBuilder(longitude, latitude)
                .build();
    }
}