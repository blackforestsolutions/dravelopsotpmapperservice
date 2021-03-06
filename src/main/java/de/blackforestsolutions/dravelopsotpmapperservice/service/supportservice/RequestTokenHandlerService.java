package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import reactor.core.publisher.Mono;

public interface RequestTokenHandlerService {
    Mono<ApiToken> getJourneyApiTokenWith(ApiToken request, ApiToken configuredRequestData);

    ApiToken getNearestStationsApiTokenWith(ApiToken request, ApiToken otpConfiguredRequestData);
}
