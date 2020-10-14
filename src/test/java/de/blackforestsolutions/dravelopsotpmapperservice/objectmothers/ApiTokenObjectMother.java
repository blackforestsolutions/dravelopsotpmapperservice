package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.data.geo.Point;

import java.time.ZonedDateTime;
import java.util.Locale;

public class ApiTokenObjectMother {

    public static ApiToken getRequestToken() {
        return new ApiToken.ApiTokenBuilder()
                .setOptimize(Optimization.QUICK)
                .setIsArrivalDateTime(false)
                .setDateTime(ZonedDateTime.parse("2020-09-30T13:00:00+02:00"))
                .setDepartureCoordinate(new Point(8.209972d, 48.048320d))
                .setDeparture("Am Großhausberg 8")
                .setArrivalCoordinate(new Point(7.950507d, 48.088204d))
                .setArrival("Sick AG")
                .build();
    }

    public static ApiToken getOpenTripPlannerConfiguredApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol("http")
                .setHost("localhost")
                .setPort(8089)
                .setRouter("bw")
                .setLanguage(Locale.forLanguageTag("de"))
                .build();
    }

    public static ApiToken getOpenTripPlannerApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol("http")
                .setHost("localhost")
                .setPort(8089)
                .setRouter("bw")
                .setLanguage(Locale.forLanguageTag("de"))
                .setOptimize(Optimization.QUICK)
                .setIsArrivalDateTime(false)
                .setDateTime(ZonedDateTime.parse("2020-09-30T13:00:00+02:00"))
                .setDeparture("Am Großhausberg 8")
                .setDepartureCoordinate(new Point(8.209972d, 48.048320d))
                .setArrival("Sick AG")
                .setArrivalCoordinate(new Point(7.950507d, 48.088204d))
                .build();
    }
}
