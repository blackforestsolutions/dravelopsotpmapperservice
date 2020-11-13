package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice.RequestTokenHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class JourneyApiServiceImpl implements JourneyApiService {

    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ExceptionHandlerService exceptionHandlerService;
    private final ApiToken openTripPlannerApiToken;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public JourneyApiServiceImpl(RequestTokenHandlerService requestTokenHandlerService, ExceptionHandlerService exceptionHandlerService, ApiToken openTripPlannerApiToken, OpenTripPlannerApiService openTripPlannerApiService) {
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.exceptionHandlerService = exceptionHandlerService;
        this.openTripPlannerApiToken = openTripPlannerApiToken;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    public Flux<Journey> retrieveJourneysFromApiService(ApiToken userRequestToken) {
        return Mono.just(userRequestToken)
                .flatMap(userToken -> requestTokenHandlerService.getRequestApiTokenWith(userToken, openTripPlannerApiToken))
                .flatMapMany(openTripPlannerApiService::getJourneysBy)
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct(Journey::getId);
    }

}
