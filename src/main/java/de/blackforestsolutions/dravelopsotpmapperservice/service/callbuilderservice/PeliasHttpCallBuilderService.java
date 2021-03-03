package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;

public interface PeliasHttpCallBuilderService {
    String buildPeliasReversePathWith(ApiToken apiToken, Point coordinate);
}
