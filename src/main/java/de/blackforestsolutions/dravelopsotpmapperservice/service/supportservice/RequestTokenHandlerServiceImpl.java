package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
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
        ApiToken.ApiTokenBuilder builderCopy = new ApiToken.ApiTokenBuilder(peliasApiToken);
        builderCopy.setLanguage(request.getLanguage());
        return builderCopy.build();
    }

    private ApiToken buildOpenTripPlannerApiTokenWith(ApiToken request, ApiToken configuredOtpData, String departure, String arrival) {
        ApiToken.ApiTokenBuilder builderCopy = new ApiToken.ApiTokenBuilder(configuredOtpData);
        builderCopy.setArrival(arrival);
        builderCopy.setArrivalCoordinate(request.getArrivalCoordinate());
        builderCopy.setDeparture(departure);
        builderCopy.setDepartureCoordinate(request.getDepartureCoordinate());
        builderCopy.setDateTime(request.getDateTime());
        builderCopy.setOptimize(request.getOptimize());
        builderCopy.setIsArrivalDateTime(request.getIsArrivalDateTime());
        builderCopy.setLanguage(request.getLanguage());
        return builderCopy.build();
    }

    private Mono<String> extractTravelPointNameFrom(ApiToken peliasApiToken, Point travelPointCoordinate, String travelPointNamePlaceholder) {
        return peliasApiService.extractTravelPointNameFrom(peliasApiToken, travelPointCoordinate)
                .flatMap(exceptionHandlerService::handleExceptions)
                .switchIfEmpty(Mono.just(travelPointNamePlaceholder));
    }

}
