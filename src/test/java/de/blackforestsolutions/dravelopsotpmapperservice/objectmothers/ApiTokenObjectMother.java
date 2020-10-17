package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.data.geo.Point;

import java.time.ZonedDateTime;
import java.util.Locale;

public class ApiTokenObjectMother {

    public static ApiToken getOtpRequestToken() {
        return new ApiToken.ApiTokenBuilder()
                .setOptimize(Optimization.QUICK)
                .setIsArrivalDateTime(false)
                .setDateTime(ZonedDateTime.parse("2020-09-30T13:00:00+02:00"))
                .setDepartureCoordinate(new Point(8.209198d, 48.048381d))
                .setArrivalCoordinate(new Point( 7.891595d, 48.087517d))
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
                .setDepartureCoordinate(new Point(8.209198d, 48.048381d))
                .setArrival("Sick AG")
                .setArrivalCoordinate(new Point( 7.891595d, 48.087517d))
                .build();
    }

    public static ApiToken getConfiguredPeliasApiToken() {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol("http")
                .setHost("localhost")
                .setPort(4000)
                .setApiVersion("v1")
                .setMaxResults(2)
                .setLanguage(Locale.forLanguageTag("de"))
                .setDeparture("Start")
                .setArrival("Ziel")
                .build();
    }
}
