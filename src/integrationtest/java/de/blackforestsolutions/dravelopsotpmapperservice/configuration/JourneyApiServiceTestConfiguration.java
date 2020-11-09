package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Point;

import java.time.ZonedDateTime;
import java.util.Locale;

@TestConfiguration
public class JourneyApiServiceTestConfiguration {

    @Value("${test.apitokens.otpmapper.optimize}")
    private Optimization optimize;
    @Value("${test.apitokens.otpmapper.isArrivalDateTime}")
    private boolean isArrivalDateTime;
    @Value("${test.apitokens.otpmapper.dateTime}")
    private String dateTime;
    @Value("${test.apitokens.otpmapper.departureCoordinateLongitude}")
    private Double departureCoordinateLongitude;
    @Value("${test.apitokens.otpmapper.departureCoordinateLatitude}")
    private Double departureCoordinateLatitude;
    @Value("${test.apitokens.otpmapper.arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens.otpmapper.arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;
    @Value("${test.apitokens.otpmapper.language}")
    private Locale language;

    @Bean
    public ApiToken.ApiTokenBuilder otpMapperApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setOptimize(optimize)
                .setIsArrivalDateTime(isArrivalDateTime)
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setDepartureCoordinate(new Point(departureCoordinateLongitude, departureCoordinateLatitude))
                .setArrivalCoordinate(new Point(arrivalCoordinateLongitude, arrivalCoordinateLatitude))
                .setLanguage(language);
    }
}