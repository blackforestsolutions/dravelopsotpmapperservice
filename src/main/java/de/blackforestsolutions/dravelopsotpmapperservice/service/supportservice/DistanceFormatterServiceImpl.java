package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.DistanceConfiguration;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;

@Service
public class DistanceFormatterServiceImpl implements DistanceFormatterService {

    private static final double METRES_TO_KILOMETRES_CONVERT_PARAMETER = 0.001d;
    private static final int MULTIPLIER = 10;


    @Override
    public Distance convertToKilometers(double meters) {
        double decimals = Math.pow(MULTIPLIER, DistanceConfiguration.NUMBER_OF_DECIMAL_PLACES);
        double kilometers = meters * METRES_TO_KILOMETRES_CONVERT_PARAMETER;
        return new Distance(Math.round(kilometers * decimals) / decimals, Metrics.KILOMETERS);
    }
}
