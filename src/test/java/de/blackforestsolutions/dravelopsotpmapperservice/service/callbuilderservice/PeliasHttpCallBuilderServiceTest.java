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
    void test_buildPeliasReversePathWith_apiToken_and_coordinate_returns_valid_path() {
        ApiToken testData = getPeliasReverseApiToken();
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasReversePathWith(testData, testPoint);

        assertThat(result).isEqualTo("/v1/reverse?point.lat=48.087517&point.lon=7.891595&size=1&lang=de&layers=venue,address,street,country,macroregion,region,macrocounty,county,locality,localadmin,borough,neighbourhood,coarse,postalcode");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_coordinate_as_null_throws_exception() {
        ApiToken testData = getPeliasReverseApiToken();

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, null));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_apiVersion_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build(), testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_maxResults_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build(), testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_language_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build(), testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_layers_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(getPeliasReverseApiToken());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData.build(), testPoint));
    }
}
