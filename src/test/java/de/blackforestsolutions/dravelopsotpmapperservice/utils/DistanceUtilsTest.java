package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceUtilsTest {

    @Test
    void test_convertMetersToKilometers_returns_correct_distance() {
        double metres = 26394.0899937427;

        Distance result = DistanceUtils.convertMetersToKilometers(metres);

        assertThat(result.getMetric()).isEqualTo(Metrics.KILOMETERS);
        assertThat(result.getValue()).isEqualTo(26.394d);
        assertThat(result.getUnit()).isEqualTo("km");
        assertThat(result).isEqualTo(new Distance(26.394d, Metrics.KILOMETERS));
    }
}
