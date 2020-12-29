package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {
    String buildOpenTripPlannerJourneyPathWith(ApiToken apiToken);
}
