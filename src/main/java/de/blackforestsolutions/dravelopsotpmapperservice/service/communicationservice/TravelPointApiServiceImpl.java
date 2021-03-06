package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.TravelPoint;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;

@Service
public class TravelPointApiServiceImpl implements TravelPointApiService {

    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final OpenTripPlannerConfiguration openTripPlannerConfiguration;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public TravelPointApiServiceImpl(RequestTokenHandlerService requestTokenHandlerService, ExceptionHandlerService exceptionHandlerService, OpenTripPlannerConfiguration openTripPlannerConfiguration, OpenTripPlannerApiService openTripPlannerApiService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.openTripPlannerConfiguration = openTripPlannerConfiguration;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    public Flux<TravelPoint> retrieveNearestStationsFromApiService(ApiToken userRequestToken) {
        return Flux.fromIterable(openTripPlannerConfiguration.getApiTokens())
                .map(this::buildApiTokenFromOtpConfiguration)
                .flatMap(otpConfigToken -> getNearestStationsBy(userRequestToken, otpConfigToken))
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct()
                .sort(Comparator.comparingDouble(travelPoint -> travelPoint.getDistanceInKilometers().getValue()))
                .take(openTripPlannerConfiguration.getMaxResults())
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

    private Flux<CallStatus<TravelPoint>> getNearestStationsBy(ApiToken userRequestToken, ApiToken otpConfigToken) {
        return Mono.just(otpConfigToken)
                .map(configToken -> requestTokenHandlerService.getNearestStationsApiTokenWith(userRequestToken, configToken))
                .flatMapMany(openTripPlannerApiService::getNearestStationsBy)
                .subscribeOn(Schedulers.parallel());
    }

    private ApiToken buildApiTokenFromOtpConfiguration(OpenTripPlannerConfiguration.ApiToken apiToken) {
        return new ApiToken.ApiTokenBuilder()
                .setProtocol(apiToken.getProtocol())
                .setHost(apiToken.getHost())
                .setPort(apiToken.getPort())
                .setRouter(apiToken.getRouter())
                .build();
    }
}
