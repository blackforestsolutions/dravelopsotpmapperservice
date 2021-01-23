package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import reactor.core.publisher.Mono;

public interface PeliasApiService {
    Mono<CallStatus<String>> extractTravelPointNameFrom(ApiToken apiToken, Point coordinate);
}
