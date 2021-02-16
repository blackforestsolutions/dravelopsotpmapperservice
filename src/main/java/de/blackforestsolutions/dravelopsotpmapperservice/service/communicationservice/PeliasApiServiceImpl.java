package de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.CallStatus;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsdatamodel.Status;
import de.blackforestsolutions.dravelopsgeneratedcontent.pelias.PeliasTravelPointResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.PeliasHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.Optional;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;

@Slf4j
@Service
public class PeliasApiServiceImpl implements PeliasApiService {

    private static final int FIRST_INDEX = 0;

    private final PeliasHttpCallBuilderService peliasHttpCallBuilderService;
    private final CallService callService;

    @Autowired
    public PeliasApiServiceImpl(PeliasHttpCallBuilderService peliasHttpCallBuilderService, CallService callService) {
        this.peliasHttpCallBuilderService = peliasHttpCallBuilderService;
        this.callService = callService;
    }

    @Override
    public Mono<CallStatus<String>> extractTravelPointNameFrom(ApiToken apiToken, Point coordinate) {
        try {
            return executeCallWith(apiToken, coordinate)
                    .map(name -> new CallStatus<>(name, Status.SUCCESS, null))
                    .onErrorResume(error -> Mono.just(new CallStatus<>(null, Status.FAILED, error)));
        } catch (Exception e) {
            return Mono.just(new CallStatus<>(null, Status.FAILED, e));
        }
    }

    private Mono<String> executeCallWith(ApiToken apiToken, Point coordinate) {
        return Mono.just(apiToken)
                .map(token -> getTravelPointNameString(token, coordinate))
                .flatMap(url -> callService.getOne(url, HttpHeaders.EMPTY, PeliasTravelPointResponse.class))
                .flatMap(this::handleEmptyResponse)
                .map(this::extractTravelPointNameFrom);
    }

    private String getTravelPointNameString(ApiToken apiToken, Point coordinate) {
        ApiToken.ApiTokenBuilder builder = new ApiToken.ApiTokenBuilder(apiToken);
        builder.setPath(peliasHttpCallBuilderService.buildPeliasTravelPointNamePathWith(apiToken, coordinate));
        URL requestUrl = buildUrlWith(builder.build());
        return requestUrl.toString();
    }

    private String extractTravelPointNameFrom(PeliasTravelPointResponse response) {
        return response.getFeatures().get(FIRST_INDEX).getProperties().getName();
    }

    private Mono<PeliasTravelPointResponse> handleEmptyResponse(PeliasTravelPointResponse response) {
        if (response.getFeatures().size() == 0) {
            Optional<Double> optionalLongitude = Optional.ofNullable(response.getGeocoding().getQuery().getPointLon());
            Optional<Double> optionalLatitude = Optional.ofNullable(response.getGeocoding().getQuery().getPointLat());

            if (optionalLongitude.isPresent() && optionalLatitude.isPresent()) {
                log.info(getPeliasNoResultLogMessageWith(optionalLongitude.get(), optionalLatitude.get()));
            } else {
                log.warn("Longitude and latitude is not available for logging a missing result!");
            }
            return Mono.empty();
        }
        return Mono.just(response);
    }

    private String getPeliasNoResultLogMessageWith(double longitude, double latitude) {
        return "No result found in pelias for longitude: "
                .concat(String.valueOf(longitude))
                .concat(" and latitude: ")
                .concat(String.valueOf(latitude));
    }
}
