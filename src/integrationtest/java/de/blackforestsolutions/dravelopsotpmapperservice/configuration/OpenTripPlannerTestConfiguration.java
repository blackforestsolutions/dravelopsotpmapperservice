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
public class OpenTripPlannerTestConfiguration {

    @Value("${otp.protocol}")
    private String protocol;
    @Value("${otp.host}")
    private String host;
    @Value("${otp.port}")
    private int port;
    @Value("${otp.router}")
    private String router;
    @Value("${test.apitokens.opentripplanner.optimize}")
    private Optimization optimize;
    @Value("${test.apitokens.opentripplanner.isArrivalDateTime}")
    private boolean isArrivalDateTime;
    @Value("${test.apitokens.opentripplanner.dateTime}")
    private String dateTime;
    @Value("${test.apitokens.opentripplanner.departure}")
    private String departure;
    @Value("${test.apitokens.opentripplanner.departureCoordinateLongitude}")
    private Double departureCoordinateLongitude;
    @Value("${test.apitokens.opentripplanner.departureCoordinateLatitude}")
    private Double departureCoordinateLatitude;
    @Value("${test.apitokens.opentripplanner.arrival}")
    private String arrival;
    @Value("${test.apitokens.opentripplanner.arrivalCoordinateLongitude}")
    private Double arrivalCoordinateLongitude;
    @Value("${test.apitokens.opentripplanner.arrivalCoordinateLatitude}")
    private Double arrivalCoordinateLatitude;
    @Value("${test.apitokens.opentripplanner.language}")
    private Locale language;

    @Bean
    public ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setRouter(router)
                .setOptimize(optimize)
                .setIsArrivalDateTime(isArrivalDateTime)
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setDeparture(departure)
                .setDepartureCoordinate(new Point(departureCoordinateLongitude, departureCoordinateLatitude))
                .setArrival(arrival)
                .setArrivalCoordinate(new Point(arrivalCoordinateLongitude, arrivalCoordinateLatitude))
                .setLanguage(language);
    }
}