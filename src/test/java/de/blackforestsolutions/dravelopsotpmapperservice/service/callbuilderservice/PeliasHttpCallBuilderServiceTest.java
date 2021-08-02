package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Layer;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

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

        assertThat(result).isEqualTo("/v1/reverse?point.lat=48.087517&point.lon=7.891595&size=1&lang=de&layers=venue,address,locality,street");
        assertThat(result).contains("venue");
        assertThat(result).contains("address");
        assertThat(result).contains("locality");
        assertThat(result).contains("street");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasVenue_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_VENUE, false);
        testData.setLayers(layers);
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasReversePathWith(testData, testPoint);

        assertThat(result).doesNotContain("venue");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasAddress_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_ADDRESS, false);
        testData.setLayers(layers);
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasReversePathWith(testData, testPoint);

        assertThat(result).doesNotContain("address");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasLocality_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_LOCALITY, false);
        testData.setLayers(layers);
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasReversePathWith(testData, testPoint);

        assertThat(result).doesNotContain("locality");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasStreet_as_false_returns_path_without_venue() {
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.put(Layer.HAS_STREET, false);
        testData.setLayers(layers);
        Point testPoint = getSickAgPoint();

        String result = classUnderTest.buildPeliasReversePathWith(testData, testPoint);

        assertThat(result).doesNotContain("street");
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_coordinate_as_null_throws_exception() {
        ApiToken testData = getPeliasReverseApiToken();

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, null));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_apiVersion_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        testData.setApiVersion(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_maxResults_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        testData.setMaxResults(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_language_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        testData.setLanguage(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_layers_as_null_and_coordinate_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        testData.setLayers(null);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasVenue_as_null_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_VENUE);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasAddress_as_null_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_ADDRESS);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasLocality_as_null_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_LOCALITY);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }

    @Test
    void test_buildPeliasReversePathWith_apiToken_and_layers_hasStreet_as_null_throws_exception() {
        Point testPoint = getSickAgPoint();
        ApiToken testData = new ApiToken(getPeliasReverseApiToken());
        LinkedHashMap<Layer, Boolean> layers = testData.getLayers();
        layers.remove(Layer.HAS_STREET);
        testData.setLayers(layers);

        assertThrows(NullPointerException.class, () -> classUnderTest.buildPeliasReversePathWith(testData, testPoint));
    }
}
