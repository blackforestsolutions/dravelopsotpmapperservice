package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceFormatterServiceTest {

    private final DistanceFormatterService classUnderTest = new DistanceFormatterServiceImpl();

    @Test
    void test_convertToKilometers_returns_correct_distance() {
        double testMetres = 26394.0899937427;

        Distance result = classUnderTest.convertToKilometers(testMetres);

        assertThat(result.getMetric()).isEqualTo(Metrics.KILOMETERS);
        assertThat(result.getValue()).isEqualTo(26.394d);
        assertThat(result.getUnit()).isEqualTo("km");
        assertThat(result).isEqualTo(new Distance(26.394d, Metrics.KILOMETERS));
    }

    @Test
    void test_convertToKilometers_with_rounding_up_returns_correct_distance() {
        double testMetres = 26394.5d;

        Distance result = classUnderTest.convertToKilometers(testMetres);

        assertThat(result.getMetric()).isEqualTo(Metrics.KILOMETERS);
        assertThat(result.getValue()).isEqualTo(26.395d);
        assertThat(result.getUnit()).isEqualTo("km");
        assertThat(result).isEqualTo(new Distance(26.395d, Metrics.KILOMETERS));
    }
}
