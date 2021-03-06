package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.*;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.Error;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.station.OpenTripPlannerStationResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Slf4j
@Service
public class OpenTripPlannerApiServiceImpl implements OpenTripPlannerApiService {

    private static final String NO_PATH_FOUND_ERROR = "PATH_NOT_FOUND";
    private static final long ERROR_404 = 404L;
    private static final String DEPARTURE = "departure";
    private static final String ARRIVAL = "arrival";

    private final OpenTripPlannerMapperService openTripPlannerMapperService;
    private final OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService;
    private final CallService callService;

    @Autowired
    public OpenTripPlannerApiServiceImpl(OpenTripPlannerMapperService openTripPlannerMapperService, OpenTripPlannerHttpCallBuilderService openTripPlannerHttpCallBuilderService, CallService callService) {
        this.openTripPlannerMapperService = openTripPlannerMapperService;
        this.openTripPlannerHttpCallBuilderService = openTripPlannerHttpCallBuilderService;
        this.callService = callService;
    }

    @Override
    public Flux<CallStatus<Journey>> getJourneysBy(ApiToken apiToken) {
        try {
            return executeJourneyCallWith(apiToken)
                    .onErrorResume(e -> Flux.just(new CallStatus<>(null, Status.FAILED, e)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    @Override
    public Flux<CallStatus<TravelPoint>> getNearestStationsBy(ApiToken apiToken) {
        try {
            return executeNearestStationsCallWith(apiToken)
                    .onErrorResume(e -> Flux.just(new CallStatus<>(null, Status.FAILED, e)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    private Flux<CallStatus<Journey>> executeJourneyCallWith(ApiToken apiToken) {
        return Mono.just(apiToken)
                .map(this::getJourneyRequestString)
                .flatMap(url -> callService.getOne(url, HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class))
                .flatMap(this::handleEmptyResponse)
                .flatMapMany(openTripPlannerJourney -> openTripPlannerMapperService.extractJourneysFrom(openTripPlannerJourney, apiToken.getDeparture(), apiToken.getArrival()));
    }

    private Flux<CallStatus<TravelPoint>> executeNearestStationsCallWith(ApiToken apiToken) {
        return Mono.just(apiToken)
                .map(this::getNearestStationsRequestString)
                .flatMapMany(url -> callService.getOne(url, HttpHeaders.EMPTY, OpenTripPlannerStationResponse[].class))
                .map(Arrays::asList)
                .flatMap(openTripPlannerMapperService::extractNearestStationFrom);
    }

    private String getJourneyRequestString(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerJourneyPathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private String getNearestStationsRequestString(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerNearestStationPathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private Mono<OpenTripPlannerJourneyResponse> handleEmptyResponse(OpenTripPlannerJourneyResponse response) {
        Optional<Error> optionalError = Optional.ofNullable(response.getError());
        if (optionalError.isPresent() && optionalError.get().getId() == ERROR_404 && optionalError.get().getMessage().equals(NO_PATH_FOUND_ERROR)) {
            log.warn(getOtpNoResultLogMessageWith(response));
            return Mono.empty();
        }
        return Mono.just(response);
    }

    private String getOtpNoResultLogMessageWith(OpenTripPlannerJourneyResponse response) {
        return "No result found in Otp from "
                .concat(response.getRequestParameters().getFromPlace())
                .concat(" to ")
                .concat(response.getRequestParameters().getToPlace())
                .concat(" when ")
                .concat(response.getRequestParameters().getDate())
                .concat("T")
                .concat(response.getRequestParameters().getTime())
                .concat(" is ")
                .concat(departureOrArrivalTime(response.getRequestParameters().getArriveBy()));
    }

    private String departureOrArrivalTime(String arriveBy) {
        if (Boolean.parseBoolean(arriveBy)) {
            return DEPARTURE;
        }
        return ARRIVAL;
    }
}
