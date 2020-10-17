package de.blackforestsolutions.dravelopsotpmapperservice.objectmothers;

import de.blackforestsolutions.dravelopsdatamodel.Optimization;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

import java.time.ZonedDateTime;
import java.util.Locale;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PointObjectMother.getAmGrosshausbergPoint;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.PointObjectMother.getSickAgPoint;

public class ApiTokenObjectMother {

    public static ApiToken getOtpRequestToken() {
        return new ApiToken.ApiTokenBuilder()
                .setOptimize(Optimization.QUICK)
                .setIsArrivalDateTime(false)
                .setDateTime(ZonedDateTime.parse("2020-09-30T13:00:00+02:00"))
                .setDepartureCoordinate(getAmGrosshausbergPoint())
                .setArrivalCoordinate(getSickAgPoint())
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
                .setDepartureCoordinate(getAmGrosshausbergPoint())
                .setArrival("Sick AG")
                .setArrivalCoordinate(getSickAgPoint())
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
