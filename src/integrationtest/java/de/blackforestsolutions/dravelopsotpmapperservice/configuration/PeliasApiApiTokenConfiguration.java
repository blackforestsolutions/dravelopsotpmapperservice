package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.util.Locale;

@TestConfiguration
public class PeliasApiApiTokenConfiguration {

    @Value("${pelias.protocol}")
    private String protocol;
    @Value("${pelias.host}")
    private String host;
    @Value("${pelias.port}")
    private int port;
    @Value("${test.apitokens.pelias.apiversion}")
    private String apiVersion;
    @Value("${test.apitokens.pelias.maxresult}")
    private int maxResult;
    @Value("${test.apitokens.pelias.departure}")
    private String departure;
    @Value("${test.apitokens.pelias.arrival}")
    private String arrival;
    @Value("${test.apitokens.pelias.language}")
    private Locale language;
    @Value("${test.apitokens.pelias.coordinate_x}")
    private double coordinate_x;
    @Value("${test.apitokens.pelias.coordinate_y}")
    private double coordinate_y;

    @Bean("peliasApiApiTokenIT")
    public ApiToken.ApiTokenBuilder peliasApiApiTokenIT() {
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
        return new Point(coordinate_x, coordinate_y);
    }
}