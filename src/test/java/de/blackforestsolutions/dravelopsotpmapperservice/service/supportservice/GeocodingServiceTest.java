package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;

import java.util.LinkedList;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.WaypointsObjectMother.getFurtwangenExampleWaypoints;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.WaypointsObjectMother.getMannheimUniversityToMannheimBerlinerPlatzWaypoints;
import static org.assertj.core.api.Assertions.assertThat;

class GeocodingServiceTest {

    private final GeocodingService classUnderTest = new GeocodingServiceImpl();

    @Test
    void test_extractCoordinateWithFixedDecimalPlacesFrom_longitude_and_latitude_returns_coordinate_with_six_fixed_decimal_places() {
        double testLongitude = 120.0000005d;
        double testLatitude = 120.0d;

        Point result = classUnderTest.extractCoordinateWithFixedDecimalPlacesFrom(testLongitude, testLatitude);

        assertThat(result).isEqualTo(new Point(120.000001d, 120.000000d));
    }

    @Test
    void test_extractKilometersFrom_meters_returns_correct_distance() {
        double testMetres = 26394.0899937427;

        Distance result = classUnderTest.extractKilometersFrom(testMetres);

        assertThat(result.getMetric()).isEqualTo(Metrics.KILOMETERS);
        assertThat(result.getValue()).isEqualTo(26.394d);
        assertThat(result.getUnit()).isEqualTo("km");
        assertThat(result).isEqualTo(new Distance(26.394d, Metrics.KILOMETERS));
    }

    @Test
    void test_extractKilometersFrom_meters_with_rounding_up_returns_correct_distance() {
        double testMetres = 26394.5d;

        Distance result = classUnderTest.extractKilometersFrom(testMetres);

        assertThat(result.getMetric()).isEqualTo(Metrics.KILOMETERS);
        assertThat(result.getValue()).isEqualTo(26.395d);
        assertThat(result.getUnit()).isEqualTo("km");
        assertThat(result).isEqualTo(new Distance(26.395d, Metrics.KILOMETERS));
    }

    @Test
    void test_decodePolylineFrom_encoded_polyline_in_furtwangen_return_correct_track() {
        String encodedPolylineTestData = "mtodHyhpo@@HVbDPVHlABAl@QRIDN|@Sd@Gx@Ob@EDp@FfAD~@@JDd@Fd@@JPt@v@{@LM";
        LinkedList<Point> expectedTrack = getFurtwangenExampleWaypoints();

        LinkedList<Point> result = classUnderTest.decodePolylineFrom(encodedPolylineTestData);

        assertThat(result).containsExactly(
                expectedTrack.get(0),
                expectedTrack.get(1),
                expectedTrack.get(2),
                expectedTrack.get(3),
                expectedTrack.get(4),
                expectedTrack.get(5),
                expectedTrack.get(6),
                expectedTrack.get(7),
                expectedTrack.get(8),
                expectedTrack.get(9),
                expectedTrack.get(10),
                expectedTrack.get(11),
                expectedTrack.get(12),
                expectedTrack.get(13),
                expectedTrack.get(14),
                expectedTrack.get(15),
                expectedTrack.get(16),
                expectedTrack.get(17),
                expectedTrack.get(18),
                expectedTrack.get(19),
                expectedTrack.get(20),
                expectedTrack.get(21),
                expectedTrack.get(22)
        );
    }

    @Test
    void test_decodePolylineFrom_encoded_polyline_return_correct_track() {
        String encodedPolylineTestData = "l`fdPnvl{U";

        LinkedList<Point> result = classUnderTest.decodePolylineFrom(encodedPolylineTestData);

        assertThat(result).containsExactly(new Point(-120.0012d, -89.984230d));
    }

    @Test
    void test_decodePolylineFrom_polyline_returns_track_between_mannheim_university_and_mannheim_berlin_place() {
        String encodedPolylineTestData = "sn_mH{~sr@i@x\\zJnp@";
        LinkedList<Point> expectedTrack = getMannheimUniversityToMannheimBerlinerPlatzWaypoints();

        LinkedList<Point> result = classUnderTest.decodePolylineFrom(encodedPolylineTestData);

        assertThat(result.size()).isEqualTo(expectedTrack.size());
        assertThat(result).containsExactly(
                expectedTrack.get(0),
                expectedTrack.get(1),
                expectedTrack.get(2)
        );
    }

}
