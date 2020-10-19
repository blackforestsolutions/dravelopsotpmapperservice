package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.ApiTokenObjectMother.getOpenTripPlannerApiToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenTripPlannerHttpCallBuilderServiceTest {

    private final OpenTripPlannerHttpCallBuilderService classUnderTest = new OpenTripPlannerHttpCallBuilderServiceImpl();

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_returns_valid_path() {
        ApiToken testData = getOpenTripPlannerApiToken();

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData);

        assertThat(result).isEqualTo("/otp/routers/bw/plan?locale=de&optimize=QUICK&arriveBy=false&date=09-30-2020&time=13:00&fromPlace=48.048381,8.209198&toPlace=48.087517,7.891595");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_english_language_returns_valid_path() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setLanguage(Locale.forLanguageTag("en"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build());

        assertThat(result).contains("locale=en");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_us_language_returns_valid_path() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setLanguage(Locale.forLanguageTag("us"));

        String result = classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build());

        assertThat(result).contains("locale=us");
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_router_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setRouter(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_language_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_optimize_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setOptimize(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_dateTime_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setDateTime(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_departureCoordinate_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setDepartureCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }

    @Test
    void test_buildOpenTripPlannerJourneyPathWith_apiToken_and_arrivalCoordinate_as_null_throws_exception() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getOpenTripPlannerApiToken());
        testData.setArrivalCoordinate(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildOpenTripPlannerJourneyPathWith(testData.build()));
    }
}
