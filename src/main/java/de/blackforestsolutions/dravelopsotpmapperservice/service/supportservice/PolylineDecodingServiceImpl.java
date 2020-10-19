package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import com.google.maps.internal.PolylineEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.stream.Collectors;


@Service
public class PolylineDecodingServiceImpl implements PolylineDecodingService {

    private final CoordinateFormatterService coordinateFormatterService;

    @Autowired
    public PolylineDecodingServiceImpl(CoordinateFormatterService coordinateFormatterService) {
        this.coordinateFormatterService = coordinateFormatterService;
    }

    @Override
    public LinkedList<Point> decode(String encodedPolyline) {
        return PolylineEncoding.decode(encodedPolyline)
                .stream()
                .map(latLng -> coordinateFormatterService.convertToPointWithFixedDecimalPlaces(latLng.lng, latLng.lat))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
