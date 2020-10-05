package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Journey;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsdatamodel.util.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsJsonMapper;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.mapperservice.OpenTripPlannerMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Service
public class OpenTripPlannerApiServiceImpl implements OpenTripPlannerApiService {

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
            return Mono.just(apiToken)
                    .map(this::getJourneyRequestString)
                    .flatMap(url -> callService.get(url, HttpHeaders.EMPTY))
                    .flatMap(httpResponse -> convert(httpResponse.getBody()))
                    .flatMapMany(openTripPlannerJourney -> openTripPlannerMapperService.extractJourneysFrom(openTripPlannerJourney, apiToken.getDeparture(), apiToken.getArrival()))
                    .onErrorResume(e -> Flux.just(new CallStatus<>(null, Status.FAILED, e)));
        } catch (Exception e) {
            return Flux.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    private String getJourneyRequestString(ApiToken apiToken) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(openTripPlannerHttpCallBuilderService.buildOpenTripPlannerJourneyPathWith(apiToken));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private Mono<OpenTripPlannerJourneyResponse> convert(String json) {
        DravelOpsJsonMapper mapper = new DravelOpsJsonMapper();
        return mapper.mapJsonToPojo(json, OpenTripPlannerJourneyResponse.class);
    }
}
