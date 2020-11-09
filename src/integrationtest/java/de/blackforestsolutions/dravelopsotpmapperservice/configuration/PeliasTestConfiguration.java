package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.util.Locale;

@TestConfiguration
public class PeliasTestConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${pelias.apiVersion}")
    private String apiVersion;
    @Value("${pelias.maxResults}")
    private int maxResult;
    @Value("${pelias.departurePlaceholder}")
    private String departure;
    @Value("${pelias.arrivalPlaceholder}")
    private String arrival;
    @Value("${test.apitokens.pelias.language}")
    private Locale language;
    @Value("${test.apitokens.pelias.coordinateLongitude}")
    private double coordinateLongitude;
    @Value("${test.apitokens.pelias.coordinateLatitude}")
    private double coordinateLatitude;

    @Bean
    public ApiToken.ApiTokenBuilder peliasReverseApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setApiVersion(apiVersion)
                .setMaxResults(maxResult)
                .setDeparture(departure)
                .setArrival(arrival)
                .setLanguage(language);
    }

    @Bean(name = "peliasPoint")
    public Point point() {
        return new Point(coordinateLongitude, coordinateLatitude);
    }
}