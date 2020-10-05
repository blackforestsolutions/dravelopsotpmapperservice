package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration;
import org.springframework.data.geo.Point;

public class CoordinateUtils {

    private static final int MULTIPLIER = 10;

    public static Point convertToPointWithFixedDecimalPlaces(double longitude, double latitude) {
        double decimals = Math.pow(MULTIPLIER, CoordinateConfiguration.NUMBER_OF_DECIMAL_PLACES);
        return new Point(Math.round(longitude * decimals) / decimals, Math.round(latitude * decimals) / decimals);
    }
}
