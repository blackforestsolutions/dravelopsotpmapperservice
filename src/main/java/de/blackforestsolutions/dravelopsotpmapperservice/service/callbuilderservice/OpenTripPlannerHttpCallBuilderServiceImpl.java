package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static de.blackforestsolutions.dravelopsotpmapperservice.configuration.DistanceConfiguration.KILOMETERS_TO_METERS_MULTIPLIER;

@Service
public class OpenTripPlannerHttpCallBuilderServiceImpl implements OpenTripPlannerHttpCallBuilderService {

    private static final String OPEN_TRIP_PLANNER_PATH = "otp";
    private static final String ROUTER_PATH = "routers";
    private static final String JOURNEY_PATH = "plan";
    private static final String INDEX_PATH = "index";
    private static final String STOP_PATH = "stops";

    private static final String DEPARTURE_OR_ARRIVAL_TIME_PARAM = "arriveBy";
    private static final String LANGUAGE_PARAM = "locale";
    private static final String DATE_PARAM = "date";
    private static final String TIME_PARAM = "time";
    private static final String DEPARTURE_PLACE_PARAM = "fromPlace";
    private static final String ARRIVAL_PLACE_PARAM = "toPlace";
    private static final String SHOW_INTERMEDIATE_STOPS_PARAM = "showIntermediateStops";
    private static final String RADIUS_IN_METERS_PARAM = "radius";
    private static final String LATITUDE_PARAM = "lat";
    private static final String LONGITUDE_PARAM = "lon";

    @Override
    public String buildOpenTripPlannerJourneyPathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getRouter(), "router is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getIsArrivalDateTime(), "isArrivalDateTime is not allowed to be null");
        Objects.requireNonNull(apiToken.getDateTime(), "dateTime is not allowed to be null");
        Objects.requireNonNull(apiToken.getDepartureCoordinate(), "departureCoordinate is not allowed to be null");
        Objects.requireNonNull(apiToken.getArrivalCoordinate(), "arrivalCoordinate is not allowed to be null");
        return "/"
                .concat(OPEN_TRIP_PLANNER_PATH)
                .concat("/")
                .concat(ROUTER_PATH)
                .concat("/")
                .concat(apiToken.getRouter())
                .concat("/")
                .concat(JOURNEY_PATH)
                .concat("?")
                .concat(LANGUAGE_PARAM)
                .concat("=")
                .concat(apiToken.getLanguage().getLanguage())
                .concat("&")
                .concat(DEPARTURE_OR_ARRIVAL_TIME_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getIsArrivalDateTime()))
                .concat("&")
                .concat(DATE_PARAM)
                .concat("=")
                .concat(apiToken.getDateTime().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")))
                .concat("&")
                .concat(TIME_PARAM)
                .concat("=")
                .concat(apiToken.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .concat("&")
                .concat(DEPARTURE_PLACE_PARAM)
                .concat("=")
                .concat(convertCoordinateToString(apiToken.getDepartureCoordinate()))
                .concat("&")
                .concat(ARRIVAL_PLACE_PARAM)
                .concat("=")
                .concat(convertCoordinateToString(apiToken.getArrivalCoordinate()));
    }

    @Override
    public String buildOpenTripPlannerNearestStationPathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getRouter(), "router is not allowed to be null");
        Objects.requireNonNull(apiToken.getRadiusInKilometers(), "radiusInKilometers is not allowed to be null");
        Objects.requireNonNull(apiToken.getArrivalCoordinate(), "arrivalCoordinate is not allowed to be null");
        return "/"
                .concat(OPEN_TRIP_PLANNER_PATH)
                .concat("/")
                .concat(ROUTER_PATH)
                .concat("/")
                .concat(apiToken.getRouter())
                .concat("/")
                .concat(INDEX_PATH)
                .concat("/")
                .concat(STOP_PATH)
                .concat("?")
                .concat(RADIUS_IN_METERS_PARAM)
                .concat("=")
                .concat(String.valueOf(convertDistanceInKilometersToLongMeters(apiToken.getRadiusInKilometers())))
                .concat("&")
                .concat(LATITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getArrivalCoordinate().getY()))
                .concat("&")
                .concat(LONGITUDE_PARAM)
                .concat("=")
                .concat(String.valueOf(apiToken.getArrivalCoordinate().getX()));
    }

    private String convertCoordinateToString(Point coordinate) {
        return String.valueOf(coordinate.getY())
                .concat(",")
                .concat(String.valueOf(coordinate.getX()));
    }

    private long convertDistanceInKilometersToLongMeters(Distance radiusInKilometers) {
        double radiusInMeters = radiusInKilometers.getValue() * KILOMETERS_TO_METERS_MULTIPLIER;
        return Math.round(radiusInMeters);
    }
}
