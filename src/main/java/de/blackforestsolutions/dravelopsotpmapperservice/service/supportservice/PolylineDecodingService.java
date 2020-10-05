package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.data.geo.Point;

import java.util.LinkedList;

public interface PolylineDecodingService {
    LinkedList<Point> decode(String encodedPolyline);
}
