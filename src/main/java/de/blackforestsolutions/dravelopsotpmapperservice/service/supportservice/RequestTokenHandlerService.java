package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;

public interface RequestTokenHandlerService {
    ApiToken getRequestApiTokenWith(ApiToken request, ApiToken configuredRequestData);
}
