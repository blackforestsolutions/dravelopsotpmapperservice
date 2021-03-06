package de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.station.OpenTripPlannerStationResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface OpenTripPlannerMapperService {
    Flux<CallStatus<Journey>> extractJourneysFrom(OpenTripPlannerJourneyResponse response, String departure, String arrival);

    Flux<CallStatus<TravelPoint>> extractNearestStationFrom(List<OpenTripPlannerStationResponse> response);
}
