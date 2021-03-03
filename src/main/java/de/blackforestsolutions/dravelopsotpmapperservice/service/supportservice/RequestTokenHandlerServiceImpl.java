package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class RequestTokenHandlerServiceImpl implements RequestTokenHandlerService {

    private final ApiToken peliasApiToken;
    private final PeliasApiService peliasApiService;
    private final ExceptionHandlerService exceptionHandlerService;

    @Autowired
    public RequestTokenHandlerServiceImpl(ApiToken peliasApiToken, PeliasApiService peliasApiService, ExceptionHandlerService exceptionHandlerService) {
        this.peliasApiToken = peliasApiToken;
        this.peliasApiService = peliasApiService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public Mono<ApiToken> getRequestApiTokenWith(ApiToken request, ApiToken otpConfiguredRequestData) {
        try {
            ApiToken peliasRequestToken = buildPeliasApiTokenWith(request, peliasApiToken);
            return Mono.zip(
                    extractTravelPointNameFrom(peliasRequestToken, request.getDepartureCoordinate(), peliasRequestToken.getDeparture()),
                    extractTravelPointNameFrom(peliasRequestToken, request.getArrivalCoordinate(), peliasRequestToken.getArrival())
            ).map(departureArrivalTuple -> buildOpenTripPlannerApiTokenWith(request, otpConfiguredRequestData, departureArrivalTuple.getT1(), departureArrivalTuple.getT2()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private ApiToken buildPeliasApiTokenWith(ApiToken request, ApiToken peliasApiToken) {
        return new ApiToken.ApiTokenBuilder(peliasApiToken)
                .setLanguage(request.getLanguage())
                .build();
    }

    private ApiToken buildOpenTripPlannerApiTokenWith(ApiToken request, ApiToken configuredOtpData, String departure, String arrival) {
        return new ApiToken.ApiTokenBuilder(configuredOtpData)
                .setArrival(arrival)
                .setArrivalCoordinate(request.getArrivalCoordinate())
                .setDeparture(departure)
                .setDepartureCoordinate(request.getDepartureCoordinate())
                .setDateTime(request.getDateTime())
                .setIsArrivalDateTime(request.getIsArrivalDateTime())
                .setLanguage(request.getLanguage())
                .build();
    }

    private Mono<String> extractTravelPointNameFrom(ApiToken peliasApiToken, Point travelPointCoordinate, String travelPointNamePlaceholder) {
        return peliasApiService.extractTravelPointNameFrom(peliasApiToken, travelPointCoordinate)
                .flatMap(exceptionHandlerService::handleExceptions)
                .switchIfEmpty(Mono.just(travelPointNamePlaceholder));
    }

}
