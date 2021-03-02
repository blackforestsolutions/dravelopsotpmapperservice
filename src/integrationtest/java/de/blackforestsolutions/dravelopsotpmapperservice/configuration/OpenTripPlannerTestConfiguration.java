package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ZonedDateTimeConfiguration.class)
@TestConfiguration
public class OpenTripPlannerTestConfiguration {

    @Value("${otp.apitokens[0].protocol}")
    private String protocol;
    @Value("${otp.apitokens[0].host}")
    private String host;
    @Value("${otp.apitokens[0].port}")
    private int port;
    @Value("${otp.apitokens[0].router}")
    private String router;
    @Value("${otp.apitokens[0].showIntermediateStops}")
    private boolean showIntermediateStops;
    @Value("${otp.apitokens[0].journeySearchWindowInMinutes}")
    private Integer journeySearchWindowInMinutes;
    @Value("${test.apitokens[0].departureCoordinateLongitude}")
    private Double departureCoordinateLongitude;
    @Value("${test.apitokens[0].departureCoordinateLatitude}")
    private Double departureCoordinateLatitude;
    @Value("${test.apitokens[0].arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens[0].arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;

    @Bean
    @ConfigurationProperties(prefix = "test.apitokens[0]")
    public ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setRouter(router)
                .setShowIntermediateStops(showIntermediateStops)
                .setJourneySearchWindowInMinutes(journeySearchWindowInMinutes)
                .setDepartureCoordinate(new Point.PointBuilder(departureCoordinateLongitude, departureCoordinateLatitude).build())
                .setArrivalCoordinate(new Point.PointBuilder(arrivalCoordinateLongitude, arrivalCoordinateLatitude).build());
    }
}