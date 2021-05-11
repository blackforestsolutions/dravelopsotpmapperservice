package de.blackforestsolutions.dravelopsotpmapperservice.configuration;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import java.time.ZonedDateTime;
import java.util.Locale;

@TestConfiguration
public class ApiServiceTestConfiguration {

    /**
     * journeyOtpMapperApiToken
     */
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.departureLatitude}")
    private Double departureLatitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.departureLongitude}")
    private Double departureLongitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.arrivalLatitude}")
    private Double arrivalLatitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.arrivalLongitude}")
    private Double arrivalLongitude;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.dateTime}")
    private String dateTime;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.isArrivalDateTime}")
    private Boolean isArrivalDateTime;
    @Value("${graphql.playground.tabs.JOURNEY_QUERY.variables.language}")
    private Locale language;

    /**
     * nearestStationsOtpMapperApiToken
     */
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.longitude}")
    private Double longitude;
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.latitude}")
    private Double latitude;
    @Value("${graphql.playground.tabs.NEAREST_STATIONS.variables.radiusInKilometers}")
    private Double radiusInKilometers;


    @Bean
    public ApiToken journeyOtpMapperApiToken() {
        ApiToken apiToken = new ApiToken();
        apiToken.setDepartureCoordinate(new Point.PointBuilder(departureLongitude, departureLatitude).build());
        apiToken.setArrivalCoordinate(new Point.PointBuilder(arrivalLongitude, arrivalLatitude).build());
        apiToken.setDateTime(ZonedDateTime.parse(dateTime));
        apiToken.setIsArrivalDateTime(isArrivalDateTime);
        apiToken.setLanguage(language);
        return apiToken;
    }

    @Bean
    public ApiToken nearestStationsOtpMapperApiToken() {
        ApiToken apiToken = new ApiToken();
        apiToken.setArrivalCoordinate(new Point.PointBuilder(longitude, latitude).build());
        apiToken.setRadiusInKilometers(new Distance(radiusInKilometers, Metrics.KILOMETERS));
        apiToken.setLanguage(language);
        return apiToken;
    }

}