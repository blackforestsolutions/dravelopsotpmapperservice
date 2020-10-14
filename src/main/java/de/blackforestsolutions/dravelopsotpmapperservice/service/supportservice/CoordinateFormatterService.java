package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.data.geo.Point;

public interface CoordinateFormatterService {
    Point convertToPointWithFixedDecimalPlaces(double longitude, double latitude);
}
