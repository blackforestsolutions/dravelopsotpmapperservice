package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.Point;
import org.springframework.data.geo.Distance;

public interface GeocodingService {
    Point extractCoordinateWithFixedDecimalPlacesFrom(double longitude, double latitude);

    Distance extractKilometersFrom(double meters);
}
