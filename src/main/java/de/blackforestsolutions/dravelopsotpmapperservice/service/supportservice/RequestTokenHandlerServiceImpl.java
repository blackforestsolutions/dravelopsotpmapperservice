package de.blackforestsolutions.dravelopsotpmapperservice.service.supportservice;

import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsotpmapperservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.PeliasApiService;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class RequestTokenHandlerServiceImpl implements RequestTokenHandlerService {

    private final ApiToken peliasApiToken;
    private final PeliasApiService peliasApiService;
    private final ExceptionHandlerService exceptionHandlerService;

    public RequestTokenHandlerServiceImpl(ApiToken peliasApiToken, PeliasApiService peliasApiService, ExceptionHandlerService exceptionHandlerService) {
        this.peliasApiToken = peliasApiToken;
        this.peliasApiService = peliasApiService;
        this.exceptionHandlerService = exceptionHandlerService;
    }

    @Override
    public Mono<ApiToken> getRequestApiTokenWith(ApiToken request, ApiToken otpConfiguredRequestData) {
        try {
            return Mono.zip(
                    extractTravelPointNameFrom(request.getDepartureCoordinate(), peliasApiToken.getDeparture()),
                    extractTravelPointNameFrom(request.getArrivalCoordinate(), peliasApiToken.getArrival())
            ).map(departureArrivalTuple -> buildApiTokenWith(request, otpConfiguredRequestData, departureArrivalTuple.getT1(), departureArrivalTuple.getT2()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private ApiToken buildApiTokenWith(ApiToken request, ApiToken configuredRequestData, String departure, String arrival) {
        ApiToken.ApiTokenBuilder builderCopy = new ApiToken.ApiTokenBuilder(configuredRequestData);
        builderCopy.setArrival(arrival);
        builderCopy.setArrivalCoordinate(request.getArrivalCoordinate());
        builderCopy.setDeparture(departure);
        builderCopy.setDepartureCoordinate(request.getDepartureCoordinate());
        builderCopy.setDateTime(request.getDateTime());
        builderCopy.setOptimize(request.getOptimize());
        builderCopy.setIsArrivalDateTime(request.getIsArrivalDateTime());
        return builderCopy.build();
    }

    private Mono<String> extractTravelPointNameFrom(Point travelPointCoordinate, String travelPointNamePlaceholder) {
        return peliasApiService.extractTravelPointNameFrom(peliasApiToken, travelPointCoordinate)
                .flatMap(exceptionHandlerService::handleExceptions)
                .switchIfEmpty(Mono.just(travelPointNamePlaceholder));
    }

}
