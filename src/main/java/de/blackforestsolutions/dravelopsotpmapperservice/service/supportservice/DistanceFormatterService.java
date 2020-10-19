package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import org.springframework.data.geo.Distance;

public interface DistanceFormatterService {
    Distance convertToKilometers(double meters);
}
