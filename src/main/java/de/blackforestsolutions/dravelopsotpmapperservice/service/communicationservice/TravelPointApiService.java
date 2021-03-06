package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import reactor.core.publisher.Flux;

public interface TravelPointApiService {
    Flux<TravelPoint> retrieveNearestStationsFromApiService(ApiToken userRequestToken);
}
