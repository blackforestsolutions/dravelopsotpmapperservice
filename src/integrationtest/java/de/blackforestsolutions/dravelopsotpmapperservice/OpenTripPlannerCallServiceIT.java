package de.blackforestsolutions.dravelopsotpmapperservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsgeneratedcontent.opentripplanner.journey.OpenTripPlannerJourneyResponse;
import de.blackforestsolutions.dravelopsotpmapperservice.configuration.OpenTripPlannerTestConfiguration;
import de.blackforestsolutions.dravelopsotpmapperservice.service.callbuilderservice.OpenTripPlannerHttpCallBuilderService;
import de.blackforestsolutions.dravelopsotpmapperservice.service.communicationservice.restcalls.CallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.util.DravelOpsHttpCallBuilder.buildUrlWith;
import static org.assertj.core.api.Assertions.assertThat;

@Import(OpenTripPlannerTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenTripPlannerCallServiceIT {

    @Autowired
    private OpenTripPlannerHttpCallBuilderService httpCallBuilderService;

    @Autowired
    private CallService callService;

    @Autowired
    private ApiToken.ApiTokenBuilder openTripPlannerApiTokenIT;

    @Test
    void test_journey() {
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiTokenIT.build());
        testData.setPath(httpCallBuilderService.buildOpenTripPlannerJourneyPathWith(testData.build()));

        Mono<OpenTripPlannerJourneyResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class);

        StepVerifier.create(result)
                .assertNext(openTripPlannerJourneyResponse -> assertThat(openTripPlannerJourneyResponse.getPlan().getItineraries().size()).isGreaterThan(0))
                .verifyComplete();
    }

    @Test
    void test_journey_with_wrong_path() {
        String testUrl = "/otp/routers/b";
        ApiToken.ApiTokenBuilder testData = new ApiToken.ApiTokenBuilder(openTripPlannerApiTokenIT.build());
        testData.setPath(testUrl);

        Mono<OpenTripPlannerJourneyResponse> result = callService.getOne(buildUrlWith(testData.build()).toString(), HttpHeaders.EMPTY, OpenTripPlannerJourneyResponse.class);

        StepVerifier.create(result)
                .expectError(WebClientResponseException.class)
                .verify();
    }
}
