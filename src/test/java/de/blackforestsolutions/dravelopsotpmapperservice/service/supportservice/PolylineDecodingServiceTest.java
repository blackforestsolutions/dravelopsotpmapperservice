package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

import java.util.LinkedList;

import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getFurtwangenExampleTrack;
import static de.blackforestsolutions.dravelopsotpmapperservice.objectmothers.TrackObjectMother.getMannheimUniversityToMannheimBerlinerPlatzTrack;
import static org.assertj.core.api.Assertions.assertThat;

class PolylineDecodingServiceTest {

    private final PolylineDecodingService classUnderTest = new PolylineDecodingServiceImpl();

    @Test
    void test_decode_with_encoded_polyline_from_furtwangen_return_correct_track() {
        String encodedPolylineTestData = "mtodHyhpo@@HVbDPVHlABAl@QRIDN|@Sd@Gx@Ob@EDp@FfAD~@@JDd@Fd@@JPt@v@{@LM";
        LinkedList<Point> expectedTrack = getFurtwangenExampleTrack();

        LinkedList<Point> result = classUnderTest.decode(encodedPolylineTestData);

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
    void test_decode_polyline_return_correct_track() {
        String encodedPolylineTestData = "l`fdPnvl{U";

        LinkedList<Point> result = classUnderTest.decode(encodedPolylineTestData);

        assertThat(result).containsExactly(new Point(-120.0012d, -89.984230d));
    }

    @Test
    void test_decode_with_polyline_returns_track_between_mannheim_university_and_mannheim_berlin_place() {
        String encodedPolylineTestData = "sn_mH{~sr@i@x\\zJnp@";
        LinkedList<Point> expectedTrack = getMannheimUniversityToMannheimBerlinerPlatzTrack();

        LinkedList<Point> result = classUnderTest.decode(encodedPolylineTestData);

        assertThat(result.size()).isEqualTo(expectedTrack.size());
        assertThat(result).containsExactly(
                expectedTrack.get(0),
                expectedTrack.get(1),
                expectedTrack.get(2)
        );

    }
}
