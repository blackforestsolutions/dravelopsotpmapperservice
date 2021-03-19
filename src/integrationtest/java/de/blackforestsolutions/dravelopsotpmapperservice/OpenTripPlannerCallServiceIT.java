package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Point;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerTestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerCallServiceIT {

    @Autowired
    private OpenTripPlannerHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken.ApiTokenBuilder journeyOtpApiToken;

    @Autowired
    private ApiToken.ApiTokenBuilder nearestStationsOtpApiToken;

    @Test
    void test_getOne_openTripPlannerJourneyResponse_with_correct_apiToken_returns_journeys() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(journeyOtpApiToken);
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerJourneyPathWith(testData.build()));

        Mono<OpenTripPlannerJourneyResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerJourneyResponse -> assertThat(openTripPlannerJourneyResponse.getPlan().getItineraries().size()).isGreaterThan(0))
                .verifyComplete();
    }

    @Test
    void test_getOne_openTripPlannerJourneyResponse_with_incorrect_apiToken_returns_zero_journeys() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(journeyOtpApiToken);
        testData.setDepartureCoordinate(new Point.PointBuilder(1.0d, 1.0d).build());
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, 0.0d).build());
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerJourneyPathWith(testData.build()));

        Mono<OpenTripPlannerJourneyResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerJourneyResponse -> assertThat(openTripPlannerJourneyResponse.getPlan().getItineraries().size()).isEqualTo(0))
                .verifyComplete();
    }

    @Test
    void test_getOne_openTripPlannerNearestStationsResponse_list_with_correct_apiToken_returns_nearest_stations() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(nearestStationsOtpApiToken);
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerNearestStationPathWith(testData.build()));

        Mono<OpenTripPlannerJourneyResponse[]> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse[].class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerJourneyResponses -> assertThat(openTripPlannerJourneyResponses.length).isGreaterThan(0))
                .verifyComplete();
    }

    @Test
    void test_getOne_openTripPlannerNearestStationsResponse_list_with_incorrect_apiToken_returns_zero_stations() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(nearestStationsOtpApiToken);
        testData.setArrivalCoordinate(new Point.PointBuilder(0.0d, -90.0d).build());
        testData.setRadiusInKilometers(new Distance(1.0d, Metrics.KILOMETERS));
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerNearestStationPathWith(testData.build()));

        Mono<OpenTripPlannerJourneyResponse[]> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse[].class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerJourneyResponses -> assertThat(openTripPlannerJourneyResponses.length).isEqualTo(0))
                .verifyComplete();
    }
}
