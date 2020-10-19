package de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import org.springframework.data.geo.Point;

public interface PeliasHttpCallBuilderService {
    String buildPeliasTravelPointNamePathWith(ApiToken apiToken, Point coordinate);
}
