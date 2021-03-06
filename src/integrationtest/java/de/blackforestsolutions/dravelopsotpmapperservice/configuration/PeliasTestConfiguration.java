package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Locale;

@Import(ConverterConfiguration.class)
@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${pelias.departurePlaceholder}")
    private String departure;
    @Value("${pelias.arrivalPlaceholder}")
    private String arrival;
    @Value("${test.apitokens[0].language}")
    private Locale language;
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private double coordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private double coordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "pelias")
    public ApiToken.ApiTokenBuilder peliasReverseApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setDeparture(departure)
                .setArrival(arrival)
                .setLanguage(language);
    }

    @Bean(name = "peliasPoint")
    public Point point() {
        return new Point.PointBuilder(coordinateLongitude, coordinateLatitude)
                .build();
    }
}