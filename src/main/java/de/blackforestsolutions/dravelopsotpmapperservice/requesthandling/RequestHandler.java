package de.blackforestsolutions.dravelopsotpmapperservice.requesthandling;

import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.JourneyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
public class RequestHandler {

    private final JourneyApiService journeyApiService;
    private final ExceptionHandlerService exceptionHandlerService;

    @Autowired
    public RequestHandler(JourneyApiService journeyApiService, ExceptionHandlerService exceptionHandlerService) {
        this.journeyApiService = journeyApiService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions
                .route(RequestPredicates.path("/otp/journeys/get"), this::retrieveOpenTripPlannerJourneys);
    }

    public Mono<ServerResponse> retrieveOpenTripPlannerJourneys(ServerRequest request) {
        return request.bodyToMono(ApiToken.class)
                .flatMap(apiToken -> ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                        .body(journeyApiService.retrieveJourneysFromApiService(apiToken), Journey.class)
                )
                .onErrorResume(exceptionHandlerService::handleExceptions)
                .switchIfEmpty(ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(Mono.empty(), Journey.class));
    }
}
