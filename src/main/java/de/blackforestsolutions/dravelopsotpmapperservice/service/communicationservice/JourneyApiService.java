package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import reactor.core.publisher.Flux;

public interface JourneyApiService {
    Flux<Journey> retrieveJourneysFromApiService(ApiToken userRequestToken);
}
