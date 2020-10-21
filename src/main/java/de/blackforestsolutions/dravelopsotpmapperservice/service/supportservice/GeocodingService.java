package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.util.LinkedList;

public interface GeocodingService {
    Point extractCoordinateWithFixedDecimalPlacesFrom(double longitude, double latitude);

    Distance extractKilometersFrom(double meters);

    LinkedList<Point> decodePolylineFrom(String encodedPolyline);
}
