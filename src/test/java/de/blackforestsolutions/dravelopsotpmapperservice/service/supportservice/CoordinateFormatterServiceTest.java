package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateFormatterServiceTest {

    private final CoordinateFormatterService classUnderTest = new CoordinateFormatterServiceImpl();

    @Test
    void test_convertToPointWithFixedDecimalPlaces_return_coordinate_with_six_fixed_decimal_places() {
        double testLongitude = 120.0000005d;
        double testLatitude = 120.0d;

        Point result = classUnderTest.convertToPointWithFixedDecimalPlaces(testLongitude, testLatitude);

        assertThat(result).isEqualTo(new Point(120.000001d, 120.000000d));
    }
}
