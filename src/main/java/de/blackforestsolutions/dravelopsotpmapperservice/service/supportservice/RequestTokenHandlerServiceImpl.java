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
    public Mono<ApiToken> getJourneyApiTokenWith(ApiToken request, ApiToken otpConfiguredRequestData) {
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

    @Override
    public ApiToken getNearestStationsApiTokenWith(ApiToken request, ApiToken otpConfiguredRequestData) {
        ApiToken nearestStationsApiToken = new ApiToken(otpConfiguredRequestData);

        nearestStationsApiToken.setArrivalCoordinate(request.getArrivalCoordinate());
        nearestStationsApiToken.setRadiusInKilometers(request.getRadiusInKilometers());
        nearestStationsApiToken.setLanguage(request.getLanguage());

        return nearestStationsApiToken;
    }

    private ApiToken buildPeliasApiTokenWith(ApiToken request, ApiToken configuredPeliasApiToken) {
        ApiToken peliasApiToken = new ApiToken(configuredPeliasApiToken);

        peliasApiToken.setLanguage(request.getLanguage());

        return peliasApiToken;
    }

    private ApiToken buildOpenTripPlannerApiTokenWith(ApiToken request, ApiToken configuredOtpData, String departure, String arrival) {
        ApiToken openTripPlannerApiToken = new ApiToken(configuredOtpData);

        openTripPlannerApiToken.setArrival(arrival);
        openTripPlannerApiToken.setArrivalCoordinate(request.getArrivalCoordinate());
        openTripPlannerApiToken.setDeparture(departure);
        openTripPlannerApiToken.setDepartureCoordinate(request.getDepartureCoordinate());
        openTripPlannerApiToken.setDateTime(request.getDateTime());
        openTripPlannerApiToken.setIsArrivalDateTime(request.getIsArrivalDateTime());
        openTripPlannerApiToken.setLanguage(request.getLanguage());

        return openTripPlannerApiToken;
    }

    private Mono<String> extractTravelPointNameFrom(ApiToken peliasApiToken, Point travelPointCoordinate, String travelPointNamePlaceholder) {
        return peliasApiService.extractTravelPointNameFrom(peliasApiToken, travelPointCoordinate)
                .flatMap(exceptionHandlerService::handleExceptions)
                .switchIfEmpty(Mono.just(travelPointNamePlaceholder));
    }

}
