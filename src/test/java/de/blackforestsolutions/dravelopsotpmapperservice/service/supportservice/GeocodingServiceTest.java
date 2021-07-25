package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import static org.assertj.core.api.Assertions.assertThat;

class GeocodingServiceTest {

    private final GeocodingService classUnderTest = new GeocodingServiceImpl();

    @Test
    void test_extractCoordinateWithFixedDecimalPlacesFrom_longitude_and_latitude_returns_coordinate_with_six_fixed_decimal_places() {
        double testLongitude = 120.0000005d;
        double testLatitude = 120.0d;

        Point result = classUnderTest.extractCoordinateWithFixedDecimalPlacesFrom(testLongitude, testLatitude);

        assertThat(result).isEqualToComparingFieldByFieldRecursively(new Point.PointBuilder(120.000001d, 120.000000d).build());
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
}
