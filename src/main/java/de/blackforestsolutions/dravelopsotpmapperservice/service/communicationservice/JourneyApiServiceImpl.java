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
                .distinct(Journey::getId)
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

    private Flux<CallStatus<Journey>> getJourneysBy(ApiToken userRequestToken, ApiToken otpConfigToken) {
        return Mono.just(otpConfigToken)
                .flatMap(otpToken -> requestTokenHandlerService.getRequestApiTokenWith(userRequestToken, otpToken))
                .flatMapMany(openTripPlannerApiService::getJourneysBy)
                .subscribeOn(Schedulers.parallel())
                .log();
    }

    private ApiToken buildApiTokenFromOtpConfiguration(OpenTripPlannerConfiguration.ApiToken apiToken) {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(apiToken.getProtocol())
                .setHost(apiToken.getHost())
                .setPort(apiToken.getPort())
                .setRouter(apiToken.getRouter())
                .setShowIntermediateStops(apiToken.getShowIntermediateStops())
                .setJourneySearchWindowInMinutes(apiToken.getJourneySearchWindowInMinutes())
                .build();
    }

}
