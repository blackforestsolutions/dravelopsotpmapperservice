package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import reactor.core.publisher.Flux;

public interface OpenTripPlannerApiService {
    Flux<CallStatus<Journey>> getJourneysBy(ApiToken apiToken);

    Flux<CallStatus<TravelPoint>> getNearestStationsBy(ApiToken apiToken);
}
