package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import reactor.core.publisher.Flux;

public interface JourneyApiService {
    Flux<String> retrieveJourneysFromApiService(String userRequestToken);
}
