package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateUtilsTest {

    @Test
    void test_convertToPointWithFixedDecimalPlaces_return_coordinate_with_six_fixed_decimal_places() {
        Point result = CoordinateUtils.convertToPointWithFixedDecimalPlaces(120.0000005d, 120.0d);

        assertThat(result).isEqualTo(new Point(120.000001d, 120.000000d));
    }
}
