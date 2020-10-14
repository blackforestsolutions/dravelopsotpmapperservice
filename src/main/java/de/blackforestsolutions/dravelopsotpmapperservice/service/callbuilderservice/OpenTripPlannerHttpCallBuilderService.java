package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public interface OpenTripPlannerHttpCallBuilderService {
    String buildOpenTripPlannerJourneyPathWith(ApiToken apiToken);
}
