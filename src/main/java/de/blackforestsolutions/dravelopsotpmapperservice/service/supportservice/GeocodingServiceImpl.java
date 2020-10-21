package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import com.google.maps.internal.PolylineEncoding;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.CoordinateConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.DistanceConfiguration;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private static final int MULTIPLIER = 10;
    private static final double METRES_TO_KILOMETRES_CONVERT_PARAMETER = 0.001d;

    @Override
    public Point extractCoordinateWithFixedDecimalPlacesFrom(double longitude, double latitude) {
        return new Point(
                roundToFixedDecimalPlaces(longitude),
                roundToFixedDecimalPlaces(latitude)
        );
    }

    @Override
    public Distance extractKilometersFrom(double meters) {
        double decimals = Math.pow(MULTIPLIER, DistanceConfiguration.NUMBER_OF_DECIMAL_PLACES);
        double kilometers = meters * METRES_TO_KILOMETRES_CONVERT_PARAMETER;
        return new Distance(Math.round(kilometers * decimals) / decimals, Metrics.KILOMETERS);
    }

    @Override
    public LinkedList<Point> decodePolylineFrom(String encodedPolyline) {
        return PolylineEncoding.decode(encodedPolyline)
                .stream()
                .map(latLng -> extractCoordinateWithFixedDecimalPlacesFrom(latLng.lng, latLng.lat))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private double roundToFixedDecimalPlaces(double coordinateHalf) {
        double decimals = Math.pow(MULTIPLIER, CoordinateConfiguration.NUMBER_OF_DECIMAL_PLACES);
        return Math.round(coordinateHalf * decimals) / decimals;
    }
}
