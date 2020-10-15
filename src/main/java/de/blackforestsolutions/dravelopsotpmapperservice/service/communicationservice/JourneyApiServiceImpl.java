package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
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

    private final DravelOpsJsonMapper dravelOpsJsonMapper = new DravelOpsJsonMapper();
    private final ExceptionHandlerService exceptionHandlerService;
    private final RequestTokenHandlerService requestTokenHandlerService;
    private final ApiToken openTripPlannerApiToken;
    private final OpenTripPlannerApiService openTripPlannerApiService;

    @Autowired
    public JourneyApiServiceImpl(ExceptionHandlerService exceptionHandlerService, RequestTokenHandlerService requestTokenHandlerService, ApiToken openTripPlannerApiToken, OpenTripPlannerApiService openTripPlannerApiService) {
        this.exceptionHandlerService = exceptionHandlerService;
        this.requestTokenHandlerService = requestTokenHandlerService;
        this.openTripPlannerApiToken = openTripPlannerApiToken;
        this.openTripPlannerApiService = openTripPlannerApiService;
    }

    @Override
    public Flux<String> retrieveJourneysFromApiService(String userRequestToken) {
        return Mono.just(userRequestToken)
                .flatMap(dravelOpsJsonMapper::mapJsonToApiToken)
                .map(userToken -> requestTokenHandlerService.getRequestApiTokenWith(userToken, openTripPlannerApiToken))
                .flatMapMany(openTripPlannerApiService::getJourneysBy)
                .flatMap(exceptionHandlerService::handleExceptions)
                .distinct(Journey::getId)
                .flatMap(dravelOpsJsonMapper::map)
                .onErrorResume(exceptionHandlerService::handleExceptions);
    }

}