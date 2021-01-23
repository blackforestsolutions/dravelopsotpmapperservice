package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.junit.jupiter.api.Test;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPeliasReverseApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.PointObjectMother.getSickAgPoint;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PeliasHttpCallBuilderServiceTest {

    private final PeliasHttpCallBuilderService classUnderTest = new PeliasHttpCallBuilderServiceImpl();

    @Test
    void test_buildPeliasTravelPointNamePathWith_apiToken_and_coordinate_returns_valid_path() {
        ApiToken testData = getPeliasReverseApiToken();
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasTravelPointNamePathWith(testData, testPoint);

        assertThat(result).isEqualTo("/v1/reverse?point.lat=48.087517&point.lon=7.891595&size=1&lang=de");
    }

    @Test
    void test_buildPeliasTravelPointNamePathWith_apiToken_and_coordinate_as_null_throws_exception() {
        ApiToken testData = getPeliasReverseApiToken();

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasTravelPointNamePathWith(testData, null));
    }

    @Test
    void buildPeliasTravelPointNamePathWith_apiToken_apiVersion_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasTravelPointNamePathWith(testData.build(), testPoint));
    }

    @Test
    void buildPeliasTravelPointNamePathWith_apiToken_maxResults_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasTravelPointNamePathWith(testData.build(), testPoint));
    }

    @Test
    void buildPeliasTravelPointNamePathWith_apiToken_language_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasTravelPointNamePathWith(testData.build(), testPoint));
    }
}
