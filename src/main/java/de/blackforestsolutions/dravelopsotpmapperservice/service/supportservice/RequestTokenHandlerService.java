package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import reactor.core.publisher.Mono;

public interface RequestTokenHandlerService {
    Mono<ApiToken> getRequestApiTokenWith(ApiToken request, ApiToken configuredRequestData);
}
