package de.blackforestsolutions.dravelopsotpmapperservice.utils;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.DistanceConfiguration;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;

public class DistanceUtils {

    private static final double METRES_TO_KILOMETRES_CONVERT_PARAMETER = 0.001d;

    public static Distance convertMetersToKilometers(double meters) {
        double decimals = Math.pow(10, DistanceConfiguration.NUMBER_OF_DECIMAL_PLACES);
        double kilometers = meters * METRES_TO_KILOMETRES_CONVERT_PARAMETER;
        return new Distance(Math.round(kilometers * decimals) / decimals, Metrics.KILOMETERS);
    }
}
