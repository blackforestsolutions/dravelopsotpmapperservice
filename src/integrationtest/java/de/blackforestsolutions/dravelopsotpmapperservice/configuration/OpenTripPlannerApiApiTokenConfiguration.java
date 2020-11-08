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
public class OpenTripPlannerApiApiTokenConfiguration {

    @Value("${test.apitokens.opentripplanner.protocol}")
    private String protocol;
    @Value("${test.apitokens.opentripplanner.host}")
    private String host;
    @Value("${test.apitokens.opentripplanner.port}")
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
    @Value("${test.apitokens.opentripplanner.departureCoordinate_x}")
    private Double departureCoordinate_x;
    @Value("${test.apitokens.opentripplanner.departureCoordinate_y}")
    private Double departureCoordinate_y;
    @Value("${test.apitokens.opentripplanner.arrival}")
    private String arrival;
    @Value("${test.apitokens.opentripplanner.arrivalCoordinate_x}")
    private Double arrivalCoordinate_x;
    @Value("${test.apitokens.opentripplanner.arrivalCoordinate_y}")
    private Double arrivalCoordinate_y;
    @Value("${test.apitokens.opentripplanner.language}")
    private Locale language;

    @Bean("openTripPlannerApiTokenIT")
    public ApiToken.ApiTokenBuilder openTripPlannerApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(protocol)
                .setHost(host)
                .setPort(port)
                .setRouter(router)
                .setOptimize(optimize)
                .setIsArrivalDateTime(isArrivalDateTime)
                .setDateTime(ZonedDateTime.parse(dateTime))
                .setDeparture(departure)
                .setDepartureCoordinate(new Point(departureCoordinate_x, departureCoordinate_y))
                .setArrival(arrival)
                .setArrivalCoordinate(new Point(arrivalCoordinate_x, arrivalCoordinate_y))
                .setLanguage(language);
    }
}