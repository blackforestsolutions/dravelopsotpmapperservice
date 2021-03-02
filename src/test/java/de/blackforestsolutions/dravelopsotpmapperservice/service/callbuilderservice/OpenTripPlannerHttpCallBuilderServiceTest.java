package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpFastLaneApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getOtpSlowLaneApiToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenTripPlannerHttpCallBuilderServiceTest {

    private final OpenTripPlannerHttpCallBuilderService classUnderTest = new OpenTripPlannerHttpCallBuilderServiceImpl();

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_fast_lane_apiToken_returns_valid_path() {
        ApiToken testData = getOtpFastLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-fast/plan?locale=de&arriveBy=false&date=09-30-2020&time=13:00&fromPlace=48.048381,8.209198&toPlace=48.087517,7.891595&showIntermediateStops=true&searchWindow=120");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_slow_lane_apiToken_returns_valid_path() {
        ApiToken testData = getOtpSlowLaneApiToken();

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw-slow/plan?locale=de&arriveBy=false&date=09-30-2020&time=13:00&fromPlace=48.048381,8.209198&toPlace=48.087517,7.891595&showIntermediateStops=true&searchWindow=1440");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_english_language_returns_valid_path() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setLanguage(Locale.forLanguageTag("en"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build());

        assertThat(result).contains("locale=en");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_us_language_returns_valid_path() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setLanguage(Locale.forLanguageTag("us"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build());

        assertThat(result).contains("locale=us");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_router_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setRouter(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_isArrivalDateTime_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setIsArrivalDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_dateTime_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_departureCoordinate_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setDepartureCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_showIntermediateStops_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setShowIntermediateStops(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_journeySearchWindowInMinutes_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOtpFastLaneApiToken());
        testData.setJourneySearchWindowInMinutes(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }
}
