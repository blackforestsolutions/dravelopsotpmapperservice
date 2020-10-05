package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import com.google.maps.internal.PolylineEncoding;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.stream.Collectors;

import static de.blackforestsolutions.dravelopsotpmapperservice.utils.CoordinateUtils.convertToPointWithFixedDecimalPlaces;

@Service
public class PolylineDecodingServiceImpl implements PolylineDecodingService {

    @Override
    public LinkedList<Point> decode(String encodedPolyline) {
        return PolylineEncoding.decode(encodedPolyline)
                .stream()
                .map(latLng -> convertToPointWithFixedDecimalPlaces(latLng.lng, latLng.lat))
                .collect(Collectors.toCollection(LinkedList::new));
    }


}
