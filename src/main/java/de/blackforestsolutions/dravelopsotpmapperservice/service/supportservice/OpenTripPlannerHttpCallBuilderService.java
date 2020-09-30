package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {
    String buildOpenTripPlannerJourneyPathWith(ApiToken apiToken);
}
