package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration;
import org.springframework.data.geo.Point;

public class CoordinateUtils {

    public static Point convertToPointWithFixedDecimalPlaces(double longitude, double latitude) {
        double decimals = Math.pow(10, CoordinateConfiguration.NUMBER_OF_DECIMAL_PLACES);
        return new Point(Math.round(longitude * decimals) / decimals, Math.round(latitude * decimals) / decimals);
    }
}
