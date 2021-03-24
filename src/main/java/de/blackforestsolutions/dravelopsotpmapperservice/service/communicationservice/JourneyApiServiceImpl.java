package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class JourneyApiServiceImpl implements JourneyApiService {

    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final OpenTripPlannerConfiguration openTripPlannerConfiguration;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public JourneyApiServiceImpl(RequestTokenHandlerService requestTokenHandlerService, ExceptionHandlerService exceptionHandlerService, OpenTripPlannerConfiguration openTripPlannerConfiguration, OpenTripPlannerApiService openTripPlannerApiService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.openTripPlannerConfiguration = openTripPlannerConfiguration;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    public Flux<Journey> retrieveJourneysFromApiService(ApiToken userRequestToken) {
        return Flux.fromIterable(openTripPlannerConfiguration.getApiTokens())
                .map(this::buildApiTokenFromOtpConfiguration)
                .flatMap(otpConfigToken -> getJourneysBy(userRequestToken, otpConfigToken))
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct()
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

    private Flux<CallStatus<Journey>> getJourneysBy(ApiToken userRequestToken, ApiToken otpConfigToken) {
        return Mono.just(otpConfigToken)
                .flatMap(otpToken -> requestTokenHandlerService.getJourneyApiTokenWith(userRequestToken, otpToken))
                .flatMapMany(openTripPlannerApiService::getJourneysBy)
                .subscribeOn(Schedulers.parallel());
    }

    private ApiToken buildApiTokenFromOtpConfiguration(OpenTripPlannerConfiguration.ApiToken apiToken) {
        ApiToken otpApiToken = new ApiToken();
        otpApiToken.setProtocol(apiToken.getProtocol());
        otpApiToken.setHost(apiToken.getHost());
        otpApiToken.setPort(apiToken.getPort());
        otpApiToken.setRouter(apiToken.getRouter());
        return otpApiToken;
    }

}
