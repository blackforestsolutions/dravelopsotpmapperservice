package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.data.geo.Distance;

import java.util.LinkedList;

public interface GeocodingService {
    LinkedList<Point> decodePolylineFrom(String encodedPolyline);

    Point extractCoordinateWithFixedDecimalPlacesFrom(double longitude, double latitude);

    Distance extractKilometersFrom(double meters);
}
