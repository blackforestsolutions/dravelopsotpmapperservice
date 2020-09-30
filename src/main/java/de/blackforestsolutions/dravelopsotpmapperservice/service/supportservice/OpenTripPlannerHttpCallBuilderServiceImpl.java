package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class OpenTripPlannerHttpCallBuilderServiceImpl implements OpenTripPlannerHttpCallBuilderService {

    private static final String OPEN_TRIP_PLANNER_PATH = "otp";
    private static final String ROUTER_PATH = "routers";
    private static final String JOURNEY_PATH = "plan";

    private static final String DEPARTURE_OR_ARRIVAL_TIME_PARAM = "arriveBy";
    private static final String LANGUAGE_PARAM = "locale";
    private static final String OPTIMIZATION_PARAM = "optimize";
    private static final String DATE_PARAM = "date";
    private static final String TIME_PARAM = "time";
    private static final String DEPARTURE_PLACE_PARAM = "fromPlace";
    private static final String ARRIVAL_PLACE_PARAM = "toPlace";

    @Override
    public String buildOpenTripPlannerJourneyPathWith(ApiToken apiToken) {
        Objects.requireNonNull(apiToken.getRouter(), "router is not allowed to be null");
        Objects.requireNonNull(apiToken.getLanguage(), "language is not allowed to be null");
        Objects.requireNonNull(apiToken.getOptimize(), "optimize is not allowed to be null");
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
                .concat(apiToken.getLanguage().toLanguageTag())
                .concat("&")
                .concat(OPTIMIZATION_PARAM)
                .concat("=")
                .concat(apiToken.getOptimize().toString())
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

    private String convertCoordinateToString(Point coordinate) {
        return String.valueOf(coordinate.getY())
                .concat(",")
                .concat(String.valueOf(coordinate.getX()));
    }
}
