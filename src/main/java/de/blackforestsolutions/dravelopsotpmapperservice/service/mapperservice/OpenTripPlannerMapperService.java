package de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import reactor.core.publisher.Flux;

public interface OpenTripPlannerMapperService {
    Flux<CallStatus<Journey>> extractJourneysFrom(OpenTripPlannerJourneyResponse response, String departure, String arrival);
}
