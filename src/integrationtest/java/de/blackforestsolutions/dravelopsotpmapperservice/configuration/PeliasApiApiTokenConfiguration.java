package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.time.ZonedDateTime;
import java.util.Locale;
// todo
@TestConfiguration
public class PeliasApiApiTokenConfiguration {

    @Value("${test.apitokens.otpmapper.protocol}")
    private String protocol;
    @Value("${test.apitokens.otpmapper.host}")
    private String host;
    @Value("${test.apitokens.otpmapper.port}")
    private int port;
    @Value("${test.apitokens.otpmapper.path}")
    private String path;
    @Value("${test.apitokens.otpmapper.optimize}")
    private Optimization optimize;
    @Value("${test.apitokens.otpmapper.isArrivalDateTime}")
    private boolean isArrivalDateTime;
    @Value("${test.apitokens.otpmapper.dateTime}")
    private String dateTime;
    @Value("${test.apitokens.otpmapper.departureCoordinate_x}")
    private Double departureCoordinate_x;
    @Value("${test.apitokens.otpmapper.departureCoordinate_y}")
    private Double departureCoordinate_y;
    @Value("${test.apitokens.otpmapper.arrivalCoordinate_x}")
    private Double arrivalCoordinate_x;
    @Value("${test.apitokens.otpmapper.arrivalCoordinate_y}")
    private Double arrivalCoordinate_y;
    @Value("${test.apitokens.otpmapper.language}")
    private Locale language;

    @Bean("otpMapperApiToken")
    public ApiToken.ApiTokenBuilder otpMapperApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setPath(path)
                .setOptimize(optimize)
                .setIsArrivalDateTime(isArrivalDateTime)
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setDepartureCoordinate(new Point(departureCoordinate_x, departureCoordinate_y))
                .setArrivalCoordinate(new Point(arrivalCoordinate_x, arrivalCoordinate_y))
                .setLanguage(language);
    }
}

// todo für die weiteren Klassen jeweil eine neue config klasse?