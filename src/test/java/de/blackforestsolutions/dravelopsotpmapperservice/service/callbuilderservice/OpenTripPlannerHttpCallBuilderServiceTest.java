package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenTripPlannerHttpCallBuilderServiceTest {

    private final OpenTripPlannerHttpCallBuilderService classUnderTest = new OpenTripPlannerHttpCallBuilderServiceImpl();

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_fast_lane_apiToken_returns_valid_path() {
        ApiToken testData = getJourneyOtpFastLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-fast/plan?locale=de&arriveBy=false&date=09-30-2020&time=13:00&fromPlace=48.048381,8.209198&toPlace=48.087517,7.891595");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_slow_lane_apiToken_returns_valid_path() {
        ApiToken testData = getJourneyOtpSlowLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-slow/plan?locale=de&arriveBy=false&date=09-30-2020&time=13:00&fromPlace=48.048381,8.209198&toPlace=48.087517,7.891595");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_english_language_returns_valid_path() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setLanguage(Locale.forLanguageTag("en"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).contains("locale=en");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_us_language_returns_valid_path() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setLanguage(Locale.forLanguageTag("us"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).contains("locale=us");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_router_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setRouter(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_isArrivalDateTime_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setIsArrivalDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_dateTime_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_departureCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setDepartureCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getJourneyOtpFastLaneApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerNearestStationPathWith_fast_lane_apiToken_returns_valid_path() {
        ApiToken testData = getNearestStationsOtpFastLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerNearestStationPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-fast/index/stops?radius=1000&lat=48.048381&lon=8.209198");
    }

    @Test
    void test_buildOpenTripPlannerNearestStationPathWith_slow_lane_apiToken_returns_valid_path() {
        ApiToken testData = getNearestStationsOtpSlowLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerNearestStationPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-slow/index/stops?radius=1000&lat=48.048381&lon=8.209198");
    }

    @Test
    void test_buildOpenTripPlannerNearestStationPathWith_apiToken_and_router_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsOtpFastLaneApiToken());
        testData.setRouter(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerNearestStationPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerNearestStationPathWith_apiToken_and_radiusInKilometers_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsOtpFastLaneApiToken());
        testData.setRadiusInKilometers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerNearestStationPathWith(testData));
    }

    @Test
    void test_buildOpenTripPlannerNearestStationPathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken testData = new ApiToken(getNearestStationsOtpFastLaneApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerNearestStationPathWith(testData));
    }
}
