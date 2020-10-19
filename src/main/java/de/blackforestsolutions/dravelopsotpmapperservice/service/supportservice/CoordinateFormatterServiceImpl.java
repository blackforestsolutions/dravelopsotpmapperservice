package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
public class CoordinateFormatterServiceImpl implements CoordinateFormatterService {

    private static final int MULTIPLIER = 10;

    @Override
    public Point convertToPointWithFixedDecimalPlaces(double longitude, double latitude) {
        return new Point(
                roundToFixedDecimalPlaces(longitude),
                roundToFixedDecimalPlaces(latitude)
        );
    }

    private double roundToFixedDecimalPlaces(double coordinateHalf) {
        double decimals = Math.pow(MULTIPLIER, CoordinateConfiguration.NUMBER_OF_DECIMAL_PLACES);
        return Math.round(coordinateHalf * decimals) / decimals;
    }
}